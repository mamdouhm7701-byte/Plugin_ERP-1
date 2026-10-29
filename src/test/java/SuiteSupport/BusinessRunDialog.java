/* شرح الملف: عرض نافذة اختيار البيئة والنشاط والإعدادات وعدد المتصفحات قبل التشغيل.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.BranchProfiles.BusinessRunRequest;
import java.util.List;
import javax.swing.*;

final class BusinessRunDialog {
    record Selection(
            BusinessRunRequest business,
            String level,
            String execution,
            BusinessRunTarget target,
            int workers) {
        Selection {
            if (workers < 1 || workers > 6)
                throw new IllegalArgumentException("اختر من1 إلى6 متصفحات");
            java.util.Objects.requireNonNull(target);
        }

        Selection(BusinessRunRequest business, String level, String execution, boolean demo) {
            this(
                    business,
                    level,
                    execution,
                    demo
                            ? BusinessRunTarget.configured("Cloud", "Demo")
                            : BusinessRunTarget.configured(
                                    Plugin_ERP.Util.PropertyReader.getProperty("Environment"),
                                    Plugin_ERP.Util.PropertyReader.getProperty("Tenant")),
                    1);
        }
    }

    private record Option<T>(String label, T value) {
        public String toString() {
            return label;
        }
    }

    private BusinessRunDialog() {}

    static Selection choose() throws Exception {
        return choose(false);
    }

    static Selection choose(boolean xmlSelection) throws Exception {
        var catalog =
                xmlSelection
                        ? XmlSuiteCatalog.load(java.nio.file.Path.of(""))
                        : List.<XmlSuiteCatalog.Entry>of();
        var result = new java.util.concurrent.CompletableFuture<Selection>();
        SwingUtilities.invokeLater(
                () -> {
                    var frame =
                            new JFrame(
                                    xmlSelection
                                            ? "ERP - XML review / اختيار ملف التشغيل"
                                            : "ERP - Business run / تشغيل حسب النشاط");
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    var activity =
                            new JComboBox<>(
                                    new Option[] {
                                        new Option<>("عام", ActivityType.PUBLIC),
                                        new Option<>("مطاعم", ActivityType.RESTAURANT),
                                        new Option<>("مقاولات", ActivityType.CONSTRUCTION),
                                        new Option<>("خرسانة", ActivityType.CONCRETE),
                                        new Option<>("معرض سيارات", ActivityType.CAR_SHOWROOM)
                                    });
                    var discount =
                            new JComboBox<>(
                                    new Option[] {
                                        new Option<>("خصم على الحركة", DiscountLevel.TRANSACTION),
                                        new Option<>("خصم على الأصناف", DiscountLevel.ITEMS)
                                    });
                    var tax =
                            new JComboBox<>(
                                    new Option[] {
                                        new Option<>("ضريبة على الصافي", TaxPolicy.NET_BASED),
                                        new Option<>(
                                                "ضريبة على الأصناف — تسمح بالإعفاء",
                                                TaxPolicy.ITEM_BASED),
                                        new Option<>("السعر شامل الضريبة", TaxPolicy.PRICE_INCLUDED)
                                    });
                    var stock =
                            new JComboBox<>(
                                    new Option[] {
                                        new Option<>("جرد دوري", StocktakingMethod.PERIODIC),
                                        new Option<>("جرد مستمر", StocktakingMethod.CONTINUOUS)
                                    });
                    var additional = new JCheckBox("ضريبة إضافية للمطاعم 100% (بعد الخصم)");
                    additional.setEnabled(false);
                    activity.addActionListener(
                            e -> {
                                boolean restaurant =
                                        ((Option<?>) activity.getSelectedItem()).value()
                                                == ActivityType.RESTAURANT;
                                additional.setEnabled(restaurant);
                                if (!restaurant) additional.setSelected(false);
                            });
                    var properties = Plugin_ERP.Util.PropertyReader.loadProperties();
                    var environment = new JComboBox<>(new String[] {"Cloud", "Test"});
                    environment.setSelectedItem(
                            Plugin_ERP.Util.PropertyReader.getProperty("Environment"));
                    var tenant = new JComboBox<String>();
                    tenant.setEditable(true);
                    var url = new JTextField(40);
                    var username = new JTextField(25);
                    var password = new JPasswordField(25);
                    var workers = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
                    Runnable refreshTenants =
                            () -> {
                                String env = environment.getSelectedItem().toString();
                                tenant.removeAllItems();
                                BusinessRunTarget.tenants(properties, env).forEach(tenant::addItem);
                                String preferred =
                                        env.equals("Test")
                                                ? "TestURL"
                                                : Plugin_ERP.Util.PropertyReader.getProperty(
                                                        "Tenant");
                                if (preferred != null) tenant.setSelectedItem(preferred);
                                url.setText(
                                        java.util.Objects.toString(
                                                properties.getProperty(
                                                        java.util.Objects.toString(
                                                                tenant.getSelectedItem(), "")),
                                                ""));
                            };
                    tenant.addActionListener(
                            e -> {
                                String key =
                                        java.util.Objects.toString(
                                                tenant.getEditor().getItem(), "");
                                url.setText(properties.getProperty(key, ""));
                            });
                    environment.addActionListener(e -> refreshTenants.run());
                    refreshTenants.run();
                    var level =
                            new JComboBox<>(
                                    new String[] {
                                        "Business checks",
                                        "Smoke",
                                        "Sanity",
                                        "E2E",
                                        "Regression",
                                        "Qualification"
                                    });
                    var xml = new JComboBox<>(catalog.toArray(XmlSuiteCatalog.Entry[]::new));
                    if (xmlSelection) {
                        catalog.stream()
                                .filter(
                                        entry ->
                                                entry.path().equals(XmlSuiteCatalog.INVOICE_REVIEW))
                                .findFirst()
                                .ifPresent(xml::setSelectedItem);
                        activity.setSelectedIndex(1);
                    }
                    var explanation = new JTextArea(5, 55);
                    explanation.setEditable(false);
                    explanation.setLineWrap(true);
                    explanation.setWrapStyleWord(true);
                    Runnable describe =
                            () -> {
                                if (xml.getSelectedItem() instanceof XmlSuiteCatalog.Entry entry) {
                                    explanation.setText(entry.description());
                                    explanation.setCaretPosition(0);
                                    tax.setEnabled(
                                            !entry.path().equals(XmlSuiteCatalog.INVOICE_REVIEW));
                                }
                            };
                    xml.addActionListener(e -> describe.run());
                    describe.run();
                    if (xmlSelection) {
                        workers.setValue(1);
                        workers.setEnabled(false);
                    }
                    var display = new JComboBox<>(new String[] {"Visible browsers", "Headless"});
                    var xmlPanel = new JPanel(new java.awt.BorderLayout(0, 6));
                    xmlPanel.add(xml, java.awt.BorderLayout.NORTH);
                    var descriptionScroll = new JScrollPane(explanation);
                    descriptionScroll.setPreferredSize(new java.awt.Dimension(640, 150));
                    xmlPanel.add(descriptionScroll, java.awt.BorderLayout.CENTER);
                    xmlPanel.add(
                            new JLabel(
                                    "XML يعمل بمتصفح واحد للحفاظ على ترتيب الحالات والإعدادات المشتركة."),
                            java.awt.BorderLayout.SOUTH);
                    var panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    List.of(
                                    new JLabel("بيئة التشغيل"),
                                    environment,
                                    new JLabel("التننت — اختار الموجود أو اكتب اسمًا جديدًا"),
                                    tenant,
                                    new JLabel("رابط البرنامج — يمكن تعديله لهذا التشغيل"),
                                    url,
                                    new JLabel(
                                            "اسم المستخدم — اتركه فارغًا لاستخدام الإعدادات المحلية"),
                                    username,
                                    new JLabel(
                                            "كلمة المرور — اتركها فارغة لاستخدام الإعدادات المحلية"),
                                    password,
                                    new JLabel("أقصى عدد متصفحات متزامنة (1–6)"),
                                    workers,
                                    new JLabel(
                                            "الحالات التي تغيّر إعدادات مشتركة تعمل منفردة؛ العدد الفعلي حسب الحالات والرام."),
                                    new JLabel("النشاط"),
                                    activity,
                                    new JLabel("مستوى الخصم"),
                                    discount,
                                    new JLabel("سياسة الضريبة — VAT 15%"),
                                    tax,
                                    new JLabel("نظام الجرد"),
                                    stock,
                                    additional,
                                    new JLabel("حزمة الاختبارات"),
                                    xmlSelection ? xmlPanel : level,
                                    new JLabel("عرض المتصفح"),
                                    display,
                                    new JLabel(
                                            "يُنشأ فرع جديد بالـAPI لكل تشغيل؛ حالات المجموعة تشترك فيه ببيانات مستقلة."),
                                    new JLabel(
                                            "Business checks تغطي العقود المنفذة فقط؛ تفاصيل النطاق تُحفظ مع نتيجة التشغيل."))
                            .forEach(
                                    component -> {
                                        component.setMaximumSize(
                                                new java.awt.Dimension(
                                                        Integer.MAX_VALUE,
                                                        component.getPreferredSize().height));
                                        component.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                                        panel.add(component);
                                        panel.add(Box.createVerticalStrut(6));
                                    });
                    var start = new JButton("تجهيز الفرع وبدء التشغيل");
                    var cancel = new JButton("إلغاء");
                    var buttons = new JPanel();
                    buttons.add(start);
                    buttons.add(cancel);
                    start.addActionListener(
                            e -> {
                                BusinessRunTarget target = null;
                                try {
                                    if (xmlSelection
                                            && xml.getSelectedItem()
                                                    instanceof XmlSuiteCatalog.Entry entry
                                            && entry.path().equals(XmlSuiteCatalog.INVOICE_REVIEW)
                                            && ((Option<?>) activity.getSelectedItem()).value()
                                                    != ActivityType.RESTAURANT)
                                        throw new IllegalArgumentException(
                                                "اختر نشاط المطاعم لمراجعة الشاشات الأربع معًا.");
                                    target =
                                            new BusinessRunTarget(
                                                    environment.getSelectedItem().toString(),
                                                    tenant.getEditor().getItem().toString(),
                                                    url.getText(),
                                                    username.getText(),
                                                    password.getPassword());
                                    target.settings();
                                    var request =
                                            new BusinessRunRequest(
                                                    (ActivityType)
                                                            ((Option<?>) activity.getSelectedItem())
                                                                    .value(),
                                                    (DiscountLevel)
                                                            ((Option<?>) discount.getSelectedItem())
                                                                    .value(),
                                                    (TaxPolicy)
                                                            ((Option<?>) tax.getSelectedItem())
                                                                    .value(),
                                                    (StocktakingMethod)
                                                            ((Option<?>) stock.getSelectedItem())
                                                                    .value(),
                                                    additional.isSelected());
                                    result.complete(
                                            new Selection(
                                                    request,
                                                    xmlSelection
                                                            ? "XML:"
                                                                    + ((XmlSuiteCatalog.Entry)
                                                                                    xml
                                                                                            .getSelectedItem())
                                                                            .path()
                                                            : level.getSelectedItem().toString(),
                                                    InteractiveTestLauncher.executionType(
                                                            display.getSelectedItem().toString()),
                                                    target,
                                                    ((Number) workers.getValue()).intValue()));
                                    password.setText("");
                                    frame.dispose();
                                } catch (IllegalArgumentException invalid) {
                                    if (target != null) target.clearSecrets();
                                    JOptionPane.showMessageDialog(
                                            frame,
                                            invalid.getMessage(),
                                            "راجع إعدادات التشغيل",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            });
                    cancel.addActionListener(
                            e -> {
                                password.setText("");
                                result.complete(null);
                                frame.dispose();
                            });
                    frame.addWindowListener(
                            new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent event) {
                                    password.setText("");
                                    result.complete(null);
                                }
                            });
                    var content = new JPanel(new java.awt.BorderLayout(0, 12));
                    content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    var scroll = new JScrollPane(panel);
                    scroll.setPreferredSize(
                            new java.awt.Dimension(
                                    720,
                                    Math.min(
                                            780,
                                            java.awt.Toolkit.getDefaultToolkit()
                                                            .getScreenSize()
                                                            .height
                                                    - 180)));
                    scroll.getVerticalScrollBar().setUnitIncrement(18);
                    content.add(scroll, java.awt.BorderLayout.CENTER);
                    content.add(buttons, java.awt.BorderLayout.SOUTH);
                    frame.setContentPane(content);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    frame.toFront();
                });
        return result.get();
    }
}
