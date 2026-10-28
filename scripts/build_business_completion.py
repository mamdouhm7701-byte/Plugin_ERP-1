# شرح الملف: يربط كل شاشة بالاختبارات الموجودة دون اعتبار ذلك إثبات نجاحها.
"""Track executable assignments separately from live evidence; never call assigned code a passed screen."""
import csv
from pathlib import Path

ACTIVITIES = ("Public", "Restaurant", "Construction", "Concrete", "CarShowroom")
SPECIAL = {
    "#/contracts/new": ("Construction", ["Invoice.ConstructionContractTest", "Invoice.ConstructionContractConversionTest"]),
    "#/car-colors": ("CarShowroom", ["CoverageTest.ActivityMasterLifecycleTest"]),
    "#/restaurant-menu": ("Restaurant", ["SpecialSalesTest.RestaurantInvoiceTest", "SpecialSalesTest.SelectedRestaurantPolicyTest", "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest", "SpecialSalesTest.RestaurantAdditionalTaxBoundaryTest", "SpecialSalesTest.RestaurantAdditionalTaxOrderTest"]),
    "#/tables": ("Restaurant", ["CoverageTest.TablesCoverageTest"]),
    "#/restaurant-orders": ("Restaurant", ["SpecialSalesTest.KitchenOrdersCoverageTest"]),
    **{route: ("Concrete", ["CoverageTest.ActivityMasterLifecycleTest"]) for route in
       ("#/cars", "#/drivers", "#/projects", "#/castingTypes", "#/cementTypes", "#/maxTemperature")},
    **{route: ("Concrete", ["Invoice.ConcreteStockJourneyTest"]) for route in
       ("#/reservations/new", "#/concrete-exchange-order/new", "#/concrete-sales-invoice/new")},
    "#/concrete-under-review/new": ("Concrete", ["Invoice.UnderReviewCoverageTest"]),
    "#/concrete-reports": ("Concrete", ["CoverageTest.ConcreteReportRequestTest", "Invoice.ConcreteReservationReportTest", "Invoice.ConcreteOperationalReportTest"]),
}


def build(rows, root):
    result = []
    for row in rows:
        route, activity = row["route"], row["activity"]
        excluded = "خارج النطاق" in row["existing_coverage"] or route.endswith("-dashboard")
        contract = SPECIAL.get(route)
        other = contract is not None and contract[0] != activity
        classes = contract[1] if contract and not other else [c.strip() for c in row["existing_test_classes"].split(";") if c.strip()]
        if route == "#/return-sales/new" and activity == "Restaurant":
            classes.append("SpecialSalesTest.RestaurantAdditionalTaxReturnTest")
        if route == "#/invoice/new" and activity == "CarShowroom":
            classes.append("Invoice.CarShowroomSalesTest")
        state = "EXCLUDED_BY_EXISTING_SCOPE" if excluded else "SPECIALIZED_FOR_OTHER_ACTIVITY" if other else "ASSIGNED"
        missing = [c for c in classes if not (root / "src/test/java" / (c.replace(".", "/") + ".java")).is_file()]
        if state == "ASSIGNED" and (not classes or missing):
            state = "MISSING_CONTRACT"
        result.append(dict(activity=activity, route=route, screen=row["screen"], assignment=state,
                           classes="; ".join(sorted(set(classes))) if not excluded and not other else "",
                           native_business_evidence=row["activity_business_validation"],
                           acceptance="NOT_COMPLETE" if not excluded and not other else "OUTSIDE_THIS_ACTIVITY_PLAN"))
    return result


def main():
    root = Path(__file__).resolve().parents[1]
    source = root / "docs/testing/ACTIVITY_SCREEN_COVERAGE.csv"
    with source.open(encoding="utf-8-sig", newline="") as stream:
        rows = build(list(csv.DictReader(stream)), root)
    output = root / "docs/testing/BUSINESS_COMPLETION_MATRIX.csv"
    with output.open("w", encoding="utf-8-sig", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=list(rows[0]))
        writer.writeheader(); writer.writerows(rows)
    missing = [r for r in rows if r["assignment"] == "MISSING_CONTRACT"]
    print(f"Tracked {len(rows)} activity/screen assignments; {len(missing)} missing class references; no new live evidence inferred")
    for row in missing:
        print(row["activity"], row["route"])


if __name__ == "__main__":
    main()
