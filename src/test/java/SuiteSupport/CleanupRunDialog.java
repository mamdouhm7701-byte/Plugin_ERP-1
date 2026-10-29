/* شرح الملف: شاشة تنظيف تشغيل سابق؛ تعرض الفرع والرابط والسجلات المتبقية قبل بدء التنظيف، وتوضح التقدم والبيانات التي تعذر حذفها. */
package SuiteSupport;

import Plugin_ERP.Util.PropertyReader;
import java.awt.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class CleanupRunDialog {
    private CleanupRunDialog() {}

    public static void main(String[] args) throws Exception {
        PropertyReader.loadProperties();
        Path project = Path.of("").toAbsolutePath().normalize();
        SwingUtilities.invokeLater(() -> load(project));
    }

    private static void load(Path project) {
        var loading = new JFrame("ERP - Cleanup / قراءة سجلات التشغيل");
        var bar = new JProgressBar();
        bar.setIndeterminate(true);
        var panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(
                new JLabel("جارٍ قراءة سجلات التشغيل المحلية؛ لا يتم حذف بيانات..."),
                BorderLayout.NORTH);
        panel.add(bar, BorderLayout.CENTER);
        loading.setContentPane(panel);
        loading.pack();
        loading.setLocationRelativeTo(null);
        loading.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        loading.setVisible(true);
        new SwingWorker<CleanupRunCatalog.Inventory, Void>() {
            protected CleanupRunCatalog.Inventory doInBackground() throws Exception {
                return CleanupRunCatalog.load(project);
            }

            protected void done() {
                if (!loading.isDisplayable()) return;
                loading.dispose();
                try {
                    show(project, get());
                } catch (Exception failure) {
                    JOptionPane.showMessageDialog(
                            null,
                            "تعذر قراءة سجلات التشغيل؛ لم يتم حذف شيء. راجع إعدادات المشروع.");
                }
            }
        }.execute();
    }

    private static void show(Path project, CleanupRunCatalog.Inventory inventory) {
        var frame = new JFrame("ERP - Cleanup / تنظيف تشغيل سابق");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var entries = new JComboBox<>(inventory.entries().toArray(CleanupRunCatalog.Entry[]::new));
        var preview = area(14);
        var progress = area(8);
        var environment = new JComboBox<>(new String[] {"Cloud", "Test"});
        var tenant = new JTextField(22);
        var username = new JTextField(22);
        var password = new JPasswordField(22);
        var deleteBranch = new JCheckBox("احذف فرع التجربة أيضًا بعد اكتمال تنظيف بياناته", true);
        var consent =
                new JCheckBox("راجعت الفرع والرابط والبيانات أعلاه؛ نظّف بيانات هذا التشغيل فقط.");
        var start = new JButton("بدء تنظيف التشغيل المختار");
        var refresh = new JButton("تحديث القائمة");
        var close = new JButton("إغلاق");
        var warnings = new JTextArea(3, 60);
        warnings.setEditable(false);
        warnings.setLineWrap(true);
        warnings.setWrapStyleWord(true);
        warnings.setText(String.join("\n", inventory.warnings()));
        Runnable describe =
                () -> {
                    consent.setSelected(false);
                    if (entries.getSelectedItem() instanceof CleanupRunCatalog.Entry entry) {
                        preview.setText(entry.preview());
                        preview.setCaretPosition(0);
                        environment.setSelectedItem(
                                entry.target()
                                        .getOrDefault(
                                                "environment",
                                                Objects.toString(
                                                        PropertyReader.getProperty("Environment"),
                                                        "Cloud")));
                        tenant.setText(
                                entry.target()
                                        .getOrDefault(
                                                "tenant",
                                                Objects.toString(
                                                        PropertyReader.getProperty("Tenant"), "")));
                    } else
                        preview.setText(
                                "لا توجد فروع تجارب مؤكدة متبقية في السجل. لا يتم عرض الفروع غير المثبتة ولا حذفها.");
                    start.setEnabled(entries.getItemCount() > 0 && consent.isSelected());
                };
        entries.addActionListener(e -> describe.run());
        consent.addActionListener(
                e -> start.setEnabled(entries.getItemCount() > 0 && consent.isSelected()));
        var controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.add(new JLabel("البيئة — لا تغير رابط الفرع المسجل"));
        controls.add(environment);
        controls.add(new JLabel("التننت — لاستخدام بيانات الدخول الصحيحة"));
        controls.add(tenant);
        controls.add(new JLabel("المستخدم — فارغ لاستخدام الإعدادات المحلية"));
        controls.add(username);
        controls.add(new JLabel("كلمة المرور — فارغة لاستخدام الإعدادات المحلية"));
        controls.add(password);
        var content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(
                new JLabel("اختر فرع التشغيل السابق؛ المعاينة لا تحذف شيئًا ولا توقف رن شغال."));
        content.add(entries);
        content.add(new JScrollPane(preview));
        content.add(controls);
        content.add(deleteBranch);
        content.add(consent);
        content.add(new JLabel("التقدم والنتيجة"));
        content.add(new JScrollPane(progress));
        content.add(new JScrollPane(warnings));
        var buttons = new JPanel();
        buttons.add(start);
        buttons.add(refresh);
        buttons.add(close);
        content.add(buttons);
        refresh.addActionListener(
                e -> {
                    try {
                        var updated = CleanupRunCatalog.load(project);
                        entries.setModel(
                                new DefaultComboBoxModel<>(
                                        updated.entries().toArray(CleanupRunCatalog.Entry[]::new)));
                        warnings.setText(String.join("\n", updated.warnings()));
                        describe.run();
                    } catch (Exception failure) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "تعذر تحديث سجلات التنظيف: " + failure.getClass().getSimpleName());
                    }
                });
        close.addActionListener(
                e -> {
                    password.setText("");
                    frame.dispose();
                });
        start.addActionListener(
                e -> {
                    var entry = (CleanupRunCatalog.Entry) entries.getSelectedItem();
                    if (entry == null || !consent.isSelected()) return;
                    BusinessRunTarget target;
                    try {
                        target =
                                new BusinessRunTarget(
                                        environment.getSelectedItem().toString(),
                                        tenant.getText(),
                                        entry.profile().expected().origin(),
                                        username.getText(),
                                        password.getPassword());
                        var credentials = target.settings();
                        credentials.clear();
                    } catch (IllegalArgumentException invalid) {
                        JOptionPane.showMessageDialog(frame, invalid.getMessage());
                        return;
                    }
                    password.setText("");
                    boolean removeBranch = deleteBranch.isSelected();
                    for (var control :
                            java.util.List.of(
                                    entries,
                                    environment,
                                    tenant,
                                    username,
                                    password,
                                    deleteBranch,
                                    consent,
                                    start,
                                    refresh,
                                    close)) control.setEnabled(false);
                    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    progress.setText("سيبدأ التنظيف بعد التحقق من عدم وجود تشغيل ERP آخر.\n");
                    new SwingWorker<CleanupRunExecution.Result, String>() {
                        protected CleanupRunExecution.Result doInBackground() throws Exception {
                            return new CleanupRunExecution()
                                    .execute(
                                            project,
                                            entry,
                                            target,
                                            removeBranch,
                                            text -> {
                                                System.out.println(text);
                                                publish(text);
                                            });
                        }

                        protected void process(java.util.List<String> lines) {
                            lines.forEach(line -> progress.append(line + "\n"));
                            progress.setCaretPosition(progress.getDocument().getLength());
                        }

                        protected void done() {
                            target.clearSecrets();
                            try {
                                var result = get();
                                boolean success =
                                        result.dataCleaned()
                                                && (!removeBranch || result.branchDeleted());
                                JOptionPane.showMessageDialog(
                                        frame,
                                        success
                                                ? "اكتمل التنظيف المطلوب. التقرير: "
                                                        + result.output()
                                                : "التنظيف لم يكتمل بالكامل؛ راجع المتبقي أدناه.\n"
                                                        + String.join("\n", result.remaining()));
                            } catch (Exception failure) {
                                Throwable cause =
                                        failure instanceof java.util.concurrent.ExecutionException
                                                ? failure.getCause()
                                                : failure;
                                String message =
                                        cause instanceof Exception ex
                                                ? XmlReviewSuiteListener.startupMessage(ex)
                                                        .replace("تشغيل XML", "التنظيف")
                                                        .replace("مراجعة XML", "التنظيف")
                                                : "تعذر إكمال التنظيف؛ سجلات الاسترجاع محفوظة.";
                                progress.append(message + "\n");
                                JOptionPane.showMessageDialog(frame, message);
                            } finally {
                                for (var control :
                                        java.util.List.of(
                                                entries,
                                                environment,
                                                tenant,
                                                username,
                                                password,
                                                deleteBranch,
                                                consent,
                                                refresh,
                                                close)) control.setEnabled(true);
                                consent.setSelected(false);
                                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                            }
                        }
                    }.execute();
                });
        describe.run();
        frame.setContentPane(content);
        frame.setSize(900, 820);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JTextArea area(int rows) {
        var area = new JTextArea(rows, 70);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
}
