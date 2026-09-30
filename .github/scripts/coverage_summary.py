"""Writes a Markdown coverage summary for a JaCoCo-format XML report (Kover or AGP JaCoCo).

Usage: python3 coverage_summary.py "<title>" <report.xml glob>

The summary goes to stdout and, on GitHub Actions, to the run summary ($GITHUB_STEP_SUMMARY).
Generated classes (Hilt, Dagger, Room, Compose compiler) are left out of the totals so the
numbers describe hand-written code only.
"""

import glob
import os
import re
import sys
import xml.etree.ElementTree as ElementTree

GENERATED_CLASS_PATTERN = re.compile(
    r"(Hilt_|_Factory|Factory\$|_Impl\b|_Impl\$|_HiltModules|_MembersInjector|"
    r"_GeneratedInjector|ComposableSingletons|hilt_aggregated_deps/|dagger/hilt/internal/|"
    r"/BuildConfig$|/R$|/R\$)"
)
LOWEST_COVERED_CLASS_COUNT = 5


def main():
    title = sys.argv[1]
    report_paths = glob.glob(sys.argv[2], recursive=True)
    if not report_paths:
        write_summary(f"### {title}\n\nNo coverage report was found at `{sys.argv[2]}`.\n")
        return
    class_counters = read_class_counters(report_paths[0])
    write_summary(format_summary(title, class_counters))


def read_class_counters(report_path):
    """Returns {class name: {"LINE": (missed, covered), "BRANCH": (missed, covered)}}."""
    report = ElementTree.parse(report_path).getroot()
    class_counters = {}
    for class_element in report.iter("class"):
        class_name = class_element.get("name")
        if GENERATED_CLASS_PATTERN.search(class_name):
            continue
        class_counters[class_name] = read_counters(class_element)
    return class_counters


def read_counters(class_element):
    counters = {"LINE": (0, 0), "BRANCH": (0, 0)}
    for counter in class_element.findall("counter"):
        counter_type = counter.get("type")
        if counter_type in counters:
            counters[counter_type] = (int(counter.get("missed")), int(counter.get("covered")))
    return counters


def format_summary(title, class_counters):
    lines = [f"### {title}", "", "| Metric | Covered | Total | Percent |", "| --- | --- | --- | --- |"]
    for counter_type, label in (("LINE", "Lines"), ("BRANCH", "Branches")):
        missed, covered = total_counter(class_counters, counter_type)
        lines.append(f"| {label} | {covered} | {missed + covered} | {percent(covered, missed)} |")
    lines.extend(format_lowest_covered_classes(class_counters))
    return "\n".join(lines) + "\n"


def total_counter(class_counters, counter_type):
    missed = sum(counters[counter_type][0] for counters in class_counters.values())
    covered = sum(counters[counter_type][1] for counters in class_counters.values())
    return missed, covered


def format_lowest_covered_classes(class_counters):
    classes_with_missed_lines = [
        (counters["LINE"][0], class_name)
        for class_name, counters in class_counters.items()
        if counters["LINE"][0] > 0
    ]
    if not classes_with_missed_lines:
        return ["", "Every line in hand-written classes is covered."]
    classes_with_missed_lines.sort(reverse=True)
    lines = ["", "Classes with the most missed lines:", ""]
    for missed_lines, class_name in classes_with_missed_lines[:LOWEST_COVERED_CLASS_COUNT]:
        lines.append(f"- `{class_name.replace('/', '.')}`: {missed_lines} missed lines")
    return lines


def percent(covered, missed):
    total = covered + missed
    if total == 0:
        return "n/a"
    return f"{100 * covered / total:.1f}%"


def write_summary(summary):
    print(summary)
    step_summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if step_summary_path:
        with open(step_summary_path, "a", encoding="utf-8") as step_summary:
            step_summary.write(summary + "\n")


if __name__ == "__main__":
    main()
