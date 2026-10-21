# إعداد Java للمشروع

المشروع يستخدم JDK 21 للبناء وتشغيل الاختبارات. تحديد `maven.compiler.release=21` وحده يحدد إصدار الكود الناتج، ولا يحدد Java التي تشغّل اختبارات Surefire. لذلك يختار `maven-toolchains-plugin` إصدارًا ضمن `[21,22)` أثناء مرحلة `validate`، ويستخدمه Compiler وSurefire تلقائيًا. يمكن أن يعمل Maven نفسه باستخدام Java 26.

## إعداد جهاز جديد

ثبّت JDK 21، مثل Eclipse Temurin 21، داخل مكان يكتشفه Maven مثل `%USERPROFILE%\.jdks` على Windows. يمكن أيضًا تعريف متغير البيئة `JAVA21_HOME` ليشير إلى مجلد JDK 21، أو استخدام ملف Maven المعتاد `~/.m2/toolchains.xml`. لا تحفظ مسار جهازك في `pom.xml`.

لفحص الإصدارات التي يكتشفها Maven:

```powershell
mvn org.apache.maven.plugins:maven-toolchains-plugin:3.2.0:display-discovered-jdk-toolchains
```

إذا لم يوجد JDK 21 يتوقف البناء في البداية بدل تشغيل اختبارات على إصدار غير متوافق. في IntelliJ، اضبط Project SDK وJRE المستخدم في تشغيل TestNG على JDK 21؛ أما تشغيل الاختبارات عبر Maven فيستخدم Toolchains من إعداد المشروع.

## التحقق

```powershell
mvn '-Dtest=RuntimeCompatibilityTest,SuiteDefinitionsTest,WorkflowCleanupOrderTest,SalesTaxProfileTest,DocumentDatesTest,SalesCalculationTest,PurchaseCalculationTest' test
mvn '-DExecutionType=LocalHeadless' '-Dsurefire.suiteXmlFiles=src/test/resources/login-testng.xml' test
```

`RuntimeCompatibilityTest` يتحقق من Java 21 ومن إنشاء خطوة Allure فعليًا عند تنفيذ دالة عليها `@Step`، كي لا يمر البناء بينما AspectJ متعطل. يظهر إصدار Java المستخدم للاختبارات أيضًا في `target/surefire-reports/TEST-*.xml` ضمن الخاصية `java.version`؛ قد يختلف عن إصدار `mvn -version`.

المراجع: [Maven Toolchains](https://maven.apache.org/plugins/maven-toolchains-plugin/select-jdk-toolchain-mojo.html)، [Surefire مع Toolchains](https://maven.apache.org/surefire/maven-surefire-plugin/examples/toolchains.html)، [Temurin](https://adoptium.net/temurin/releases/?version=21).
