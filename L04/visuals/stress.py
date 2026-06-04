import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from pathlib import Path
from tabulate import tabulate

BASE_DIR = Path("results")
STRESS_DIR = BASE_DIR / "load-test-2"

MAX_RESPONSE_TIME_MS = 890
TIME_WINDOW_SECONDS = 10
STEP = 5

csv_files = sorted(STRESS_DIR.glob("*stress-test*.csv"))

if not csv_files:
    raise FileNotFoundError(f"Не найден CSV стресс-теста в папке: {STRESS_DIR}")

csv_file = csv_files[-1]
df = pd.read_csv(csv_file)

df["timeStamp"] = pd.to_datetime(df["timeStamp"], unit="ms")
df["elapsed"] = pd.to_numeric(df["elapsed"], errors="coerce")
df["success"] = df["success"].astype(str).str.lower() == "true"
df["responseCode"] = df["responseCode"].astype(str)

df["load"] = df["label"].astype(str).str.extract(r"(\d+)").astype(float)
df = df.dropna(subset=["elapsed", "load"]).copy()
df["load"] = df["load"].astype(int)

summary_rows = []

for load in sorted(df["load"].unique()):
    load_df = df[df["load"] == load].copy()

    duration_seconds = (
        load_df["timeStamp"].max() - load_df["timeStamp"].min()
    ).total_seconds()

    actual_load = len(load_df) / duration_seconds * 60 if duration_seconds > 0 else 0
    average_response = load_df["elapsed"].mean()
    p95_response = load_df["elapsed"].quantile(0.95)
    p99_response = load_df["elapsed"].quantile(0.99)
    above_limit_percent = (load_df["elapsed"] > MAX_RESPONSE_TIME_MS).mean() * 100

    summary_rows.append({
        "Ступень нагрузки": load,
        "Среднее время отклика": average_response,
        "95-й процентиль": p95_response,
        "99-й процентиль": p99_response,
    })

summary_df = pd.DataFrame(summary_rows)
summary_df = summary_df.sort_values("Ступень нагрузки").reset_index(drop=True)

critical_load_exact = None
critical_load_step = None
interpolated_rows = []

for index in range(1, len(summary_df)):
    previous_row = summary_df.loc[index - 1]
    current_row = summary_df.loc[index]

    previous_load = previous_row["Ступень нагрузки"]
    current_load = current_row["Ступень нагрузки"]

    previous_p95 = previous_row["95-й процентиль"]
    current_p95 = current_row["95-й процентиль"]

    if previous_p95 <= MAX_RESPONSE_TIME_MS < current_p95:
        critical_load_exact = previous_load + (
            (MAX_RESPONSE_TIME_MS - previous_p95)
            / (current_p95 - previous_p95)
        ) * (current_load - previous_load)

        critical_load_step = int(np.ceil(critical_load_exact / STEP) * STEP)

        start_load = int(previous_load) + STEP
        end_load = critical_load_step

        for interpolated_load in range(start_load, end_load + STEP, STEP):
            ratio = (interpolated_load - previous_load) / (current_load - previous_load)

            interpolated_average = previous_row["Среднее время отклика"] + ratio * (
                current_row["Среднее время отклика"] - previous_row["Среднее время отклика"]
            )

            interpolated_p95 = previous_row["95-й процентиль"] + ratio * (
                current_row["95-й процентиль"] - previous_row["95-й процентиль"]
            )

            interpolated_p99 = previous_row["99-й процентиль"] + ratio * (
                current_row["99-й процентиль"] - previous_row["99-й процентиль"]
            )

            interpolated_rows.append({
                "Ступень нагрузки": interpolated_load,
                "Среднее время отклика": interpolated_average,
                "95-й процентиль": interpolated_p95,
                "99-й процентиль": interpolated_p99
            })

        break

if critical_load_step is None:
    failed_rows = summary_df[summary_df["95-й процентиль"] > MAX_RESPONSE_TIME_MS]

    if not failed_rows.empty:
        critical_load_exact = float(failed_rows.iloc[0]["Ступень нагрузки"])
        critical_load_step = int(np.ceil(critical_load_exact / STEP) * STEP)

if interpolated_rows:
    interpolated_df = pd.DataFrame(interpolated_rows)
    output_df = pd.concat([summary_df, interpolated_df], ignore_index=True)
else:
    output_df = summary_df.copy()

output_df = output_df.sort_values("Ступень нагрузки").reset_index(drop=True)

if critical_load_step is not None:
    output_df["Соответствует требованию"] = np.where(
        output_df["Ступень нагрузки"].astype(float) <= critical_load_step,
        "Да",
        "Нет"
    )
else:
    output_df["Соответствует требованию"] = "Да"

for column in [
    "Среднее время отклика",
    "95-й процентиль",
    "99-й процентиль"
]:
    output_df[column] = pd.to_numeric(output_df[column], errors="coerce").round(2)

print()
print("=" * 150)
print("СВОДНАЯ ТАБЛИЦА СТРЕСС-ТЕСТИРОВАНИЯ")
print(f"Файл: {csv_file}")
print("=" * 150)

print(
    tabulate(
        output_df,
        headers="keys",
        tablefmt="rounded_grid",
        showindex=False,
        numalign="right",
        stralign="center",
        disable_numparse=True
    )
)

plt.rcParams.update({
    "font.size": 8,
    "axes.titlesize": 9,
    "axes.labelsize": 8,
    "legend.fontsize": 6.5,
    "xtick.labelsize": 7,
    "ytick.labelsize": 7,
    "figure.titlesize": 12
})

figure_width = 12.5
figure_height = 7.5

fig1, axes1 = plt.subplots(2, 2, figsize=(figure_width, figure_height))
fig1.canvas.manager.set_window_title("Стресс-тестирование: производительность")
fig1.suptitle(
    "Стресс-тестирование: деградация производительности",
    fontweight="bold",
    fontsize=12
)

df["is_sla_violation"] = df["elapsed"] > MAX_RESPONSE_TIME_MS

load_order = sorted(df["load"].unique())

ax = axes1[0, 0]

load_profile_df = (
    df.groupby("load")
    .agg(
        requests=("elapsed", "count"),
        duration_sec=("timeStamp", lambda x: (x.max() - x.min()).total_seconds())
    )
    .reset_index()
)

load_profile_df["actual_rpm"] = np.where(
    load_profile_df["duration_sec"] > 0,
    load_profile_df["requests"] / load_profile_df["duration_sec"] * 60,
    0
)

ax.plot(
    load_profile_df["load"],
    load_profile_df["actual_rpm"],
    marker="o",
    linewidth=2,
    label="Фактическая нагрузка"
)

ax.plot(
    load_profile_df["load"],
    load_profile_df["load"],
    marker="o",
    linewidth=2,
    linestyle="--",
    label="Плановая нагрузка"
)

if critical_load_step is not None:
    ax.axvline(
        critical_load_step,
        linestyle=":",
        linewidth=1.8,
        label=f"Порог {critical_load_step} зап/мин"
    )

ax.set_title("Плановая и фактическая нагрузка")
ax.set_xlabel("Ступень нагрузки, запросов/мин")
ax.set_ylabel("Запросов/мин")
ax.grid(True, alpha=0.35)
ax.legend(loc="upper left", framealpha=0.9)

ax = axes1[0, 1]

sla_margin_df = summary_df.copy()
sla_margin_df["Запас среднего"] = MAX_RESPONSE_TIME_MS - sla_margin_df["Среднее время отклика"]
sla_margin_df["Запас P95"] = MAX_RESPONSE_TIME_MS - sla_margin_df["95-й процентиль"]
sla_margin_df["Запас P99"] = MAX_RESPONSE_TIME_MS - sla_margin_df["99-й процентиль"]

ax.plot(
    sla_margin_df["Ступень нагрузки"],
    sla_margin_df["Запас среднего"],
    marker="o",
    linewidth=2,
    label="Среднее"
)

ax.plot(
    sla_margin_df["Ступень нагрузки"],
    sla_margin_df["Запас P95"],
    marker="o",
    linewidth=2,
    label="P95"
)

ax.plot(
    sla_margin_df["Ступень нагрузки"],
    sla_margin_df["Запас P99"],
    marker="o",
    linewidth=2,
    label="P99"
)

ax.axhline(
    0,
    linestyle="--",
    linewidth=1.5,
    label="Граница SLA"
)

if critical_load_step is not None:
    ax.axvline(
        critical_load_step,
        linestyle=":",
        linewidth=1.8,
        label=f"Порог {critical_load_step} зап/мин"
    )

ax.set_title("Запас до SLA по нагрузке")
ax.set_xlabel("Ступень нагрузки, запросов/мин")
ax.set_ylabel("Запас, мс")
ax.grid(True, alpha=0.35)
ax.legend(loc="upper left", framealpha=0.9)

ax = axes1[1, 0]

stability_df = (
    df.groupby("load")
    .agg(
        std_ms=("elapsed", "std"),
        iqr_ms=("elapsed", lambda x: x.quantile(0.75) - x.quantile(0.25))
    )
    .reset_index()
)

ax.plot(
    stability_df["load"],
    stability_df["std_ms"],
    marker="o",
    linewidth=2,
    label="Стандартное отклонение"
)

ax.plot(
    stability_df["load"],
    stability_df["iqr_ms"],
    marker="o",
    linewidth=2,
    label="IQR"
)

if critical_load_step is not None:
    ax.axvline(
        critical_load_step,
        linestyle=":",
        linewidth=1.8,
        label=f"Порог {critical_load_step} зап/мин"
    )

ax.set_title("Стабильность времени отклика")
ax.set_xlabel("Ступень нагрузки, запросов/мин")
ax.set_ylabel("Разброс, мс")
ax.grid(True, alpha=0.35)
ax.legend(loc="upper left", framealpha=0.9)

ax = axes1[1, 1]

efficiency_df = summary_df.copy()
efficiency_df["Запросов на 1 мс"] = efficiency_df["Ступень нагрузки"] / efficiency_df["Среднее время отклика"]

ax.plot(
    efficiency_df["Ступень нагрузки"],
    efficiency_df["Запросов на 1 мс"],
    marker="o",
    linewidth=2,
    label="Нагрузка / средний отклик"
)

if critical_load_step is not None:
    ax.axvline(
        critical_load_step,
        linestyle=":",
        linewidth=1.8,
        label=f"Порог {critical_load_step} зап/мин"
    )

ax.set_title("Эффективность обработки нагрузки")
ax.set_xlabel("Ступень нагрузки, запросов/мин")
ax.set_ylabel("Запросов/мин на 1 мс")
ax.grid(True, alpha=0.35)
ax.legend(loc="upper left", framealpha=0.9)

fig1.subplots_adjust(
    left=0.07,
    right=0.97,
    bottom=0.08,
    top=0.90,
    hspace=0.34,
    wspace=0.22
)

fig2, axes2 = plt.subplots(2, 2, figsize=(figure_width, figure_height))
fig2.canvas.manager.set_window_title("Стресс-тестирование: анализ SLA")
fig2.suptitle(
    "Стресс-тестирование: анализ нарушений SLA",
    fontweight="bold",
    fontsize=12
)

df["is_sla_violation"] = df["elapsed"] > MAX_RESPONSE_TIME_MS
df["is_successful_sla"] = ~df["is_sla_violation"]

ax = axes2[0, 0]

sla_by_load = (
    df.groupby("load")
    .agg(
        sla_violation_percent=("is_sla_violation", lambda x: x.mean() * 100),
        successful_sla_percent=("is_successful_sla", lambda x: x.mean() * 100)
    )
    .reset_index()
)

ax.plot(
    sla_by_load["load"],
    sla_by_load["sla_violation_percent"],
    marker="o",
    linewidth=2,
    label="Нарушили SLA"
)

ax.plot(
    sla_by_load["load"],
    sla_by_load["successful_sla_percent"],
    marker="o",
    linewidth=2,
    label="В пределах SLA"
)

if critical_load_step is not None:
    ax.axvline(
        critical_load_step,
        linestyle=":",
        linewidth=1.8,
        label=f"Порог {critical_load_step} зап/мин"
    )

ax.set_title("Доля запросов по SLA")
ax.set_xlabel("Ступень нагрузки, запросов/мин")
ax.set_ylabel("Доля запросов, %")
ax.grid(True, alpha=0.35)
ax.legend(loc="upper left", framealpha=0.9)

ax = axes2[0, 1]

sla_counts = (
    df.groupby("load")
    .agg(
        within_sla=("is_successful_sla", "sum"),
        violated_sla=("is_sla_violation", "sum")
    )
    .reset_index()
)

x = np.arange(len(sla_counts))
width = 0.35

ax.bar(
    x - width / 2,
    sla_counts["within_sla"],
    width,
    label="В пределах SLA"
)

ax.bar(
    x + width / 2,
    sla_counts["violated_sla"],
    width,
    label="Нарушили SLA"
)

ax.set_xticks(x)
ax.set_xticklabels(sla_counts["load"].astype(str))
ax.set_title("Количество запросов по SLA")
ax.set_xlabel("Ступень нагрузки, запросов/мин")
ax.set_ylabel("Количество запросов")
ax.grid(axis="y", alpha=0.35)
ax.legend(loc="upper left", framealpha=0.9)

ax = axes2[1, 0]

violation_df = df[df["is_sla_violation"]].copy()

if not violation_df.empty:
    violation_df["excess_ms"] = violation_df["elapsed"] - MAX_RESPONSE_TIME_MS

    violation_bins = pd.cut(
        violation_df["excess_ms"],
        bins=[0, 25, 50, 100, 200, 500, np.inf],
        labels=[
            "0-25",
            "25-50",
            "50-100",
            "100-200",
            "200-500",
            ">500"
        ],
        right=True
    )

    violation_distribution = (
        violation_bins
        .value_counts()
        .sort_index()
    )

    ax.barh(
        violation_distribution.index.astype(str),
        violation_distribution.values
    )

    ax.set_title("Насколько превышен лимит 890 мс")
    ax.set_xlabel("Количество запросов")
    ax.set_ylabel("Превышение")
    ax.grid(axis="x", alpha=0.35)
else:
    ax.text(
        0.5,
        0.5,
        "Нарушений SLA нет",
        ha="center",
        va="center",
        transform=ax.transAxes
    )
    ax.set_title("Насколько превышен лимит 890 мс")
    ax.axis("off")

ax = axes2[1, 1]

status_summary = pd.Series({
    "В пределах SLA": int((~df["is_sla_violation"]).sum()),
    "Нарушили SLA": int(df["is_sla_violation"].sum())
})

ax.pie(
    status_summary.values,
    labels=status_summary.index,
    autopct=lambda p: f"{p:.1f}%" if p > 0 else "",
    startangle=90
)

ax.set_title("Итоговое распределение по SLA")

fig2.subplots_adjust(
    left=0.07,
    right=0.97,
    bottom=0.08,
    top=0.90,
    hspace=0.34,
    wspace=0.25
)

fig3, ax = plt.subplots(figsize=(10, 6))
fig3.canvas.manager.set_window_title("Стресс-тестирование: пользователи и время ответа")

users_response_df = summary_df.copy()
users_response_df["Количество пользователей"] = (
    users_response_df["Ступень нагрузки"] / 40
)

ax.plot(
    users_response_df["Количество пользователей"],
    users_response_df["Среднее время отклика"],
    marker="o",
    linewidth=2,
    label="Среднее время отклика"
)

ax.plot(
    users_response_df["Количество пользователей"],
    users_response_df["95-й процентиль"],
    marker="o",
    linewidth=2,
    label="95-й процентиль"
)

ax.plot(
    users_response_df["Количество пользователей"],
    users_response_df["99-й процентиль"],
    marker="o",
    linewidth=2,
    label="99-й процентиль"
)

ax.axhline(
    MAX_RESPONSE_TIME_MS,
    linestyle="--",
    linewidth=1.5,
    label=f"SLA {MAX_RESPONSE_TIME_MS} мс"
)

if critical_load_step is not None:
    ax.axvline(
        critical_load_step / 40,
        linestyle=":",
        linewidth=1.8,
        label=f"Порог 765 зап/мин"
    )

ax.set_title("Зависимость времени ответа от количества пользователей")
ax.set_xlabel("Количество пользователей")
ax.set_ylabel("Время ответа, мс")
ax.grid(True, alpha=0.35)
ax.legend(loc="upper left", framealpha=0.9)

fig3.subplots_adjust(
    left=0.09,
    right=0.97,
    bottom=0.11,
    top=0.90
)

plt.show()