# نماذج الحسابات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-2a24a7347cbc"></a>
## DiscountType.java

**المصدر:** [src/main/java/Plugin_ERP/Models/DiscountType.java](../../../src/main/java/Plugin_ERP/Models/DiscountType.java)

**دوره:** نوع الخصم: قيمة أم نسبة؛ ليس رقم الخصم نفسه.

**حالته:** مصدر نشط · **عدد أسطره:** 6

**ملفات تشير إليه مباشرة:** [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

<a id="file-dff4d2713009"></a>
## InvoiceRow.java

**المصدر:** [src/main/java/Plugin_ERP/Models/InvoiceRow.java](../../../src/main/java/Plugin_ERP/Models/InvoiceRow.java)

**دوره:** نموذج بند حسابي: اسم وكمية وسعر وقيمة بند.

**حالته:** مصدر نشط · **عدد أسطره:** 32

**ملفات تشير إليه مباشرة:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [DocumentColumnEditing.java](MAIN_PAGES_Shared_AR.md#file-25745f5f5d3c)، [DocumentGridEditing.java](MAIN_PAGES_Shared_AR.md#file-14d530217dad)، [DocumentItemControls.java](MAIN_PAGES_Shared_AR.md#file-90e4c2a522d6)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)، [DocumentPaymentControls.java](MAIN_PAGES_Shared_AR.md#file-06066f1bdac6)، [DocumentPrintChecks.java](MAIN_PAGES_Shared_AR.md#file-e15f8897987f)، [DocumentQuantityPrintCheck.java](MAIN_PAGES_Shared_AR.md#file-6ab0f716fbfb)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public InvoiceRow(String itemName, BigDecimal quantity, BigDecimal price)` | public | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public String getItemName()` | public | قراءة أو إرجاع الصنف / الاسم، حسب المدخلات والتخصص. |
| `public BigDecimal getQuantity()` | public | قراءة أو إرجاع الكمية، حسب المدخلات والتخصص. |
| `public BigDecimal getPrice()` | public | قراءة أو إرجاع السعر، حسب المدخلات والتخصص. |
| `public BigDecimal getLineTotal()` | public | قراءة أو إرجاع البند / الإجمالي، حسب المدخلات والتخصص. |

<a id="file-027b46379317"></a>
## InvoiceTotals.java

**المصدر:** [src/main/java/Plugin_ERP/Models/InvoiceTotals.java](../../../src/main/java/Plugin_ERP/Models/InvoiceTotals.java)

**دوره:** نموذج الإجماليات المتوقع حسابيًا: الإجمالي والخصم وقبل الضريبة والضريبة والصافي.

**حالته:** مصدر نشط · **عدد أسطره:** 68

**ملفات تشير إليه مباشرة:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [DocumentColumnEditing.java](MAIN_PAGES_Shared_AR.md#file-25745f5f5d3c)، [DocumentGridEditing.java](MAIN_PAGES_Shared_AR.md#file-14d530217dad)، [DocumentItemControls.java](MAIN_PAGES_Shared_AR.md#file-90e4c2a522d6)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)، [DocumentPaymentControls.java](MAIN_PAGES_Shared_AR.md#file-06066f1bdac6)، [DocumentPrintChecks.java](MAIN_PAGES_Shared_AR.md#file-e15f8897987f)، [DocumentQuantityPrintCheck.java](MAIN_PAGES_Shared_AR.md#file-6ab0f716fbfb)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public BigDecimal getTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |
| `public void setTotal(BigDecimal total)` | public | تعيين الإجمالي، حسب المدخلات والتخصص. |
| `public BigDecimal getDiscount()` | public | قراءة أو إرجاع الخصم، حسب المدخلات والتخصص. |
| `public void setDiscount(BigDecimal discount)` | public | تعيين الخصم، حسب المدخلات والتخصص. |
| `public BigDecimal getBeforeTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public void setBeforeTax(BigDecimal beforeTax)` | public | تعيين الضريبة، حسب المدخلات والتخصص. |
| `public BigDecimal getTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public void setTax(BigDecimal tax)` | public | تعيين الضريبة، حسب المدخلات والتخصص. |
| `public BigDecimal getNet()` | public | قراءة أو إرجاع الصافي، حسب المدخلات والتخصص. |
| `public void setNet(BigDecimal net)` | public | تعيين الصافي، حسب المدخلات والتخصص. |
| `public String toString()` | public | تنفيذ جزء متعلق بـ القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |

<a id="file-26aee4dee40f"></a>
## ReportTotals.java

**المصدر:** [src/main/java/Plugin_ERP/Models/ReportTotals.java](../../../src/main/java/Plugin_ERP/Models/ReportTotals.java)

**دوره:** نموذج قيم الإجماليات المقروءة من التقرير المطبوع.

**حالته:** مصدر نشط · **عدد أسطره:** 52

**ملفات تشير إليه مباشرة:** [AbstractForSales.java](MAIN_PAGES_Shared_AR.md#file-30b503deea8e)، [DocumentColumnEditing.java](MAIN_PAGES_Shared_AR.md#file-25745f5f5d3c)، [DocumentGridEditing.java](MAIN_PAGES_Shared_AR.md#file-14d530217dad)، [DocumentItemControls.java](MAIN_PAGES_Shared_AR.md#file-90e4c2a522d6)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)، [DocumentPaymentControls.java](MAIN_PAGES_Shared_AR.md#file-06066f1bdac6)، [DocumentPrintChecks.java](MAIN_PAGES_Shared_AR.md#file-e15f8897987f)، [DocumentQuantityPrintCheck.java](MAIN_PAGES_Shared_AR.md#file-6ab0f716fbfb)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `public BigDecimal getTotal()` | public | قراءة أو إرجاع الإجمالي، حسب المدخلات والتخصص. |
| `public void setTotal(BigDecimal total)` | public | تعيين الإجمالي، حسب المدخلات والتخصص. |
| `public BigDecimal getDiscount()` | public | قراءة أو إرجاع الخصم، حسب المدخلات والتخصص. |
| `public void setDiscount(BigDecimal discount)` | public | تعيين الخصم، حسب المدخلات والتخصص. |
| `public BigDecimal getBeforeTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public void setBeforeTax(BigDecimal beforeTax)` | public | تعيين الضريبة، حسب المدخلات والتخصص. |
| `public BigDecimal getTax()` | public | قراءة أو إرجاع الضريبة، حسب المدخلات والتخصص. |
| `public void setTax(BigDecimal tax)` | public | تعيين الضريبة، حسب المدخلات والتخصص. |
| `public BigDecimal getNet()` | public | قراءة أو إرجاع الصافي، حسب المدخلات والتخصص. |
| `public void setNet(BigDecimal net)` | public | تعيين الصافي، حسب المدخلات والتخصص. |

<a id="file-4601a98d3779"></a>
## TaxMode.java

**المصدر:** [src/main/java/Plugin_ERP/Models/TaxMode.java](../../../src/main/java/Plugin_ERP/Models/TaxMode.java)

**دوره:** هل الأسعار شاملة الضريبة أم غير شاملة في المسار الحسابي المعني.

**حالته:** مصدر نشط · **عدد أسطره:** 6

**ملفات تشير إليه مباشرة:** [DocumentColumnEditing.java](MAIN_PAGES_Shared_AR.md#file-25745f5f5d3c)، [DocumentPage.java](MAIN_PAGES_Shared_AR.md#file-68b4b1149fce)، [DocumentPaymentControls.java](MAIN_PAGES_Shared_AR.md#file-06066f1bdac6)، [DocumentPrintChecks.java](MAIN_PAGES_Shared_AR.md#file-e15f8897987f)، [DocumentSummaryReader.java](MAIN_PAGES_Shared_AR.md#file-0b7db83fde59)، [InvoiceBusinessValidation.java](MAIN_PAGES_Shared_AR.md#file-79ba3d4eb127)، [PurchaseInvoiceSnapshot.java](MAIN_PAGES_Shared_AR.md#file-09dda0ec1286)، [PropertyReader.java](MAIN_Utiel_AR.md#file-94d06e1d47d8)؛ العرض مختصر وليس قائمة كاملة.

**الميثودات المعلنة:** لا توجد ميثودات عادية استخرجها الحصر. قد يكون الملف enum/record أو واجهة أو كلاسًا يعتمد على الأب أو ملفًا فارغًا/معلقًا؛ راجع حالته والمصدر.

