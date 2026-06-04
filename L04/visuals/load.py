import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path
from tabulate import tabulate

BASE_DIR = Path("results")

CONFIGS = {
    "Config 1 ($1300)": BASE_DIR / "load-test-1",
    "Config 2 ($2400)": BASE_DIR / "load-test-2",
    "Config 3 ($3700)": BASE_DIR / "load-test-3",
}

MAX_RESPONSE_TIME_MS = 890
REQUIRED_THROUGHPUT = 560

COLORS = {
    "Config 1 ($1300)": "#E76F51",
    "Config 2 ($2400)": "#F4A261",
    "Config 3 ($3700)": "#2A9D8F",
}

LIMIT_COLOR = "#264653"

data = {}

for name, directory in CONFIGS.items():
    csv_file = next(directory.glob("*load-test*.csv"))
    df = pd.read_csv(csv_file)

    df["timeStamp"] = pd.to_datetime(df["timeStamp"], unit="ms")
    df["elapsed"] = pd.to_numeric(df["elapsed"], errors="coerce")
    df["success"] = df["success"].astype(str).str.lower() == "true"

    df = df.dropna(subset=["timeStamp", "elapsed"]).copy()

    df["second"] = (
        df["timeStamp"] - df["timeStamp"].min()
    ).dt.total_seconds().astype(int)

    data[name] = df

summary = []

for name, df in data.items():
    duration = (
        df["timeStamp"].max() - df["timeStamp"].min()
    ).total_seconds()

    requests_per_minute = (
        len(df) / duration * 60
        if duration > 0
        else 0
    )

    avg_response = df["elapsed"].mean()
    median_response = df["elapsed"].median()
    p90_response = df["elapsed"].quantile(0.90)
    p95_response = df["elapsed"].quantile(0.95)
    p99_response = df["elapsed"].quantile(0.99)
    max_response = df["elapsed"].max()
    success_rate = df["success"].mean() * 100

    time_requirement_met = avg_response <= MAX_RESPONSE_TIME_MS

    throughput_diff_percent = (
        (requests_per_minute - REQUIRED_THROUGHPUT)
        / REQUIRED_THROUGHPUT
        * 100
    )

    throughput_diff_absolute = requests_per_minute - REQUIRED_THROUGHPUT
    time_delta_ms = MAX_RESPONSE_TIME_MS - avg_response

    summary.append({
        "config": name,
        "requests": len(df),
        "duration_sec": round(duration, 2),
        "success_rate_%": round(success_rate, 2),
        "avg_response_ms": round(avg_response, 2),
        "median_response_ms": round(median_response, 2),
        "p90_response_ms": round(p90_response, 2),
        "p95_response_ms": round(p95_response, 2),
        "p99_response_ms": round(p99_response, 2),
        "max_response_ms": round(max_response, 2),
        "throughput_rpm": round(requests_per_minute, 2),
        "time_requirement_met": time_requirement_met,
        "throughput_diff_%": round(throughput_diff_percent, 2),
        "throughput_diff_rpm": round(throughput_diff_absolute, 2),
        "time_delta_ms": round(time_delta_ms, 2)
    })

summary_df = pd.DataFrame(summary)


def signed_number(value, digits=2):
    if value > 0:
        return f"+{value:.{digits}f}"
    if value < 0:
        return f"{value:.{digits}f}"
    return f"0.{'0' * digits}"


report = pd.DataFrame({
    "Конфиг": summary_df["config"],
    "Запросы": summary_df["requests"],
    "Длительность, сек": summary_df["duration_sec"],
    "Успех %": summary_df["success_rate_%"],
    "Среднее, мс": summary_df["avg_response_ms"],
    "Медиана, мс": summary_df["median_response_ms"],
    "P95, мс": summary_df["p95_response_ms"],
    "P99, мс": summary_df["p99_response_ms"],
    "Зап/мин": summary_df["throughput_rpm"],
    "Δ от 890, мс": summary_df["time_delta_ms"].apply(
        lambda x: signed_number(x, 0)
    ),
    "Подходит": summary_df["time_requirement_met"].map({
        True: "Да",
        False: "Нет"
    })
})

print(
    tabulate(
        report,
        headers="keys",
        tablefmt="rounded_grid",
        showindex=False,
        numalign="right",
        stralign="center",
        disable_numparse=True
    )
)

fig, axes = plt.subplots(2, 2, figsize=(17, 10))

fig.suptitle(
    "Сравнение конфигураций при нагрузочном тестировании",
    fontsize=16,
    fontweight="bold"
)

for_name_order = list(data.keys())

ax = axes[0][0]

for name in for_name_order:
    df = data[name]
    throughput = df.groupby("second").size() * 60
    throughput = throughput.rolling(window=10, min_periods=1).mean()

    ax.plot(
        throughput.index,
        throughput.values,
        linewidth=2.5,
        label=name,
        color=COLORS[name]
    )

ax.axhline(
    y=REQUIRED_THROUGHPUT,
    linestyle="--",
    linewidth=2,
    label="Требуемая нагрузка 560 зап/мин",
    color=LIMIT_COLOR
)

ax.set_title("Пропускная способность")
ax.set_xlabel("Время, сек")
ax.set_ylabel("Запросов в минуту")
ax.grid(True, alpha=0.35)
ax.legend(loc="lower right")

ax = axes[0][1]

for name in for_name_order:
    df = data[name]
    response_time = df.groupby("second")["elapsed"].mean()
    response_time = response_time.rolling(window=10, min_periods=1).mean()

    ax.plot(
        response_time.index,
        response_time.values,
        linewidth=2.5,
        label=name,
        color=COLORS[name]
    )

ax.axhline(
    y=MAX_RESPONSE_TIME_MS,
    linestyle="--",
    linewidth=2,
    label="Лимит 890 мс",
    color=LIMIT_COLOR
)

ax.set_title("Среднее время отклика")
ax.set_xlabel("Время, сек")
ax.set_ylabel("Время отклика, мс")
ax.grid(True, alpha=0.35)
ax.legend(loc="upper right")

ax = axes[1][0]

bar_colors = [
    COLORS[name]
    for name in summary_df["config"]
]

bars = ax.bar(
    summary_df["config"],
    summary_df["avg_response_ms"],
    color=bar_colors
)

ax.axhline(
    y=MAX_RESPONSE_TIME_MS,
    linestyle="--",
    linewidth=2,
    label="Лимит 890 мс",
    color=LIMIT_COLOR
)

for bar in bars:
    height = bar.get_height()
    ax.text(
        bar.get_x() + bar.get_width() / 2,
        height + 18,
        f"{height:.0f}",
        ha="center",
        fontsize=10
    )

ax.set_title("Среднее время отклика по конфигурациям")
ax.set_ylabel("Время отклика, мс")
ax.grid(axis="y", alpha=0.35)
ax.legend(loc="upper right")

ax = axes[1][1]

for name in for_name_order:
    df = data[name].sort_values("timeStamp").copy()

    failures_per_second = (
        (~df["success"])
        .groupby(df["second"])
        .sum()
    )

    cumulative_failures = failures_per_second.cumsum()

    ax.plot(
        cumulative_failures.index,
        cumulative_failures.values,
        linewidth=2.5,
        label=name,
        color=COLORS[name]
    )

ax.set_title("Накопленное количество ошибок")
ax.set_xlabel("Время, сек")
ax.set_ylabel("Ошибок накоплено")
ax.grid(True, alpha=0.35)
ax.legend(loc="lower right")

plt.tight_layout()
plt.show()