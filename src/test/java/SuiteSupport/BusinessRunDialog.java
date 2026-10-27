package SuiteSupport;

import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.BranchProfiles.BusinessRunRequest;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;

/** Native IntelliJ entry; selections remain scoped to this JVM run. */
final class BusinessRunDialog {
    record Selection(BusinessRunRequest business, String level, String execution, boolean demo) {}

    private record Option<T>(String label, T value) {
        public String toString() {
            return label;
        }
    }

    private BusinessRunDialog() {}

    static Selection choose() throws Exception {
        var result = new java.util.concurrent.CompletableFuture<Selection>();
        SwingUtilities.invokeLater(
                () -> {
                    var frame = new JFrame("ERP - Business run / تشغيل حسب النشاط");
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
                    var environment =
                            new JComboBox<>(
                                    new String[] {
                                        "Demo — Cloud", "إعدادات البيئة الحالية في المشروع"
                                    });
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
                    var display = new JComboBox<>(new String[] {"Visible browsers", "Headless"});
                    var panel = new JPanel(new GridLayout(0, 1, 6, 6));
                    List.of(
                                    new JLabel("بيئة التشغيل"),
                                    environment,
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
                                    level,
                                    new JLabel("عرض المتصفح"),
                                    display,
                                    new JLabel(
                                            "يُستخدم فرع اختبار مطابق أو يُنشأ فرع مستقل؛ لا تُعدل سياسات الفروع القديمة."),
                                    new JLabel(
                                            "Business checks تغطي العقود المنفذة فقط؛ تفاصيل النطاق تُحفظ مع نتيجة التشغيل."))
                            .forEach(panel::add);
                    var start = new JButton("تجهيز الفرع وبدء التشغيل");
                    var cancel = new JButton("إلغاء");
                    var buttons = new JPanel();
                    buttons.add(start);
                    buttons.add(cancel);
                    start.addActionListener(
                            e -> {
                                var request =
                                        new BusinessRunRequest(
                                                (ActivityType)
                                                        ((Option<?>) activity.getSelectedItem())
                                                                .value(),
                                                (DiscountLevel)
                                                        ((Option<?>) discount.getSelectedItem())
                                                                .value(),
                                                (TaxPolicy)
                                                        ((Option<?>) tax.getSelectedItem()).value(),
                                                (StocktakingMethod)
                                                        ((Option<?>) stock.getSelectedItem())
                                                                .value(),
                                                additional.isSelected());
                                result.complete(
                                        new Selection(
                                                request,
                                                level.getSelectedItem().toString(),
                                                InteractiveTestLauncher.executionType(
                                                        display.getSelectedItem().toString()),
                                                environment.getSelectedIndex() == 0));
                                frame.dispose();
                            });
                    cancel.addActionListener(
                            e -> {
                                result.complete(null);
                                frame.dispose();
                            });
                    frame.addWindowListener(
                            new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent event) {
                                    result.complete(null);
                                }
                            });
                    var content = new JPanel(new java.awt.BorderLayout(0, 12));
                    content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    content.add(panel, java.awt.BorderLayout.CENTER);
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
