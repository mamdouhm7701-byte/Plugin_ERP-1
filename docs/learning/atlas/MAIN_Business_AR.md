# حاسبة الأعمال

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-e5f4eb40b205"></a>
## InvoiceCalculator.java

**المصدر:** [src/main/java/Plugin_ERP/Business/Calculator/InvoiceCalculator.java](../../../src/main/java/Plugin_ERP/Business/Calculator/InvoiceCalculator.java)

**دوره:** حساب إجماليات الفاتورة والخصم والضريبة الشاملة وغير الشاملة والتقريب بشكل مستقل عن الشاشة.

**حالته:** مصدر نشط · **عدد أسطره:** 160

**يرتبط مباشرة بملفات داخل المشروع:** [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**ملفات تشير إليه مباشرة:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [DocumentColumnEditing.java](MAIN_PAGES_Shared_AR.md#file-25745f5f5d3c)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)، [DocumentPaymentControls.java](MAIN_PAGES_Shared_AR.md#file-06066f1bdac6)، [DocumentPrintChecks.java](MAIN_PAGES_Shared_AR.md#file-e15f8897987f)، [DocumentSummaryReader.java](MAIN_PAGES_Shared_AR.md#file-0b7db83fde59)، [Invoice.java](MAIN_PAGES_Invoice_AR.md#file-a0ad56722888)، [InvoiceBusinessValidation.java](MAIN_PAGES_Shared_AR.md#file-79ba3d4eb127)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public static InvoiceTotals calculate( List<InvoiceRow> rows, BigDecimal discount, DiscountType discountType, BigDecimal taxPercentage, TaxMode taxMode, int precision)` | public | احتساب القيم من البنود والسياسة المعطاة أو الإعدادات. |
| `private static BigDecimal round(BigDecimal value, int precision)` | private | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
| `private static InvoiceTotals calculateExclusive( List<InvoiceRow> rows, BigDecimal discount, DiscountType discountType, BigDecimal taxPercentage, int precision)` | private | حساب حالة الأسعار غير الشاملة للضريبة. |
| `private static InvoiceTotals calculateInclusive( List<InvoiceRow> rows, BigDecimal discount, DiscountType discountType, BigDecimal taxPercentage, int precision)` | private | حساب حالة الأسعار الشاملة للضريبة. |
| `public static InvoiceTotals calculate( List<InvoiceRow> rows, BigDecimal discount, DiscountType discountType, BigDecimal taxPercentage, int precision)` | public | احتساب القيم من البنود والسياسة المعطاة أو الإعدادات. |
| `public static InvoiceTotals calculate(List<InvoiceRow> rows, BigDecimal discount)` | public | احتساب القيم من البنود والسياسة المعطاة أو الإعدادات. |

