# تعداد المسارات

[فهرس الأطلس](README_AR.md) · [خريطة البنية](../03_ARCHITECTURE_AR.md)

اقرأ الدور أولًا، ثم الواجهة والعلاقات. الميثودات الموروثة تُراجع في بطاقة الأب، ولا تتكرر هنا. القيم السرية ونصوص بيانات الدخول لا تُنسخ.

<a id="file-0cf000b33a7e"></a>
## Pages.java

**المصدر:** [src/main/java/Plugin_ERP/Enums/Pages.java](../../../src/main/java/Plugin_ERP/Enums/Pages.java)

**دوره:** دليل مسارات شاشات ERP المسماة في الكود؛ وجود route لا يثبت وجود تغطية كاملة له.

**حالته:** مصدر نشط · **عدد أسطره:** 59

**يرتبط مباشرة بملفات داخل المشروع:** [AreaPage.java](MAIN_PAGES_BasicData_AR.md#file-3f7bf8c58091)

**ملفات تشير إليه مباشرة:** [BrowserAction.java](MAIN_Bots_AR.md#file-ca0683510b61)، [Abstract.java](MAIN_PAGES_Shared_AR.md#file-f230c0052300)، [CashFormReader.java](MAIN_PAGES_BasicData_AR.md#file-0efcc911edbb)، [CashPage.java](MAIN_PAGES_BasicData_AR.md#file-ed12e0c67c34)، [StorePage.java](MAIN_PAGES_BasicData_AR.md#file-ccf2392eb482)، [WarehouseRecords.java](MAIN_PAGES_BasicData_AR.md#file-18db193b049f)، [MainDealer.java](MAIN_PAGES_DealerModel_AR.md#file-f6e2daf7a1c7)، [MainDealerRecords.java](MAIN_PAGES_DealerModel_AR.md#file-6c3436b3356b)؛ العرض مختصر وليس قائمة كاملة.

### خريطة الميثودات المعلنة

| التوقيع كما في المصدر | الإتاحة | كيف تقرأ الغرض؟ |
|---|---|---|
| `Pages(String route)` | داخل الحزمة | منشئ الكلاس: يستقبل الاعتماديات ويجهز الكائن عند إنشائه؛ ليس حالة اختبار. |
| `public String getRoute()` | public | قراءة أو إرجاع القدرة التي يحددها اسم الدالة، حسب المدخلات والتخصص. |
