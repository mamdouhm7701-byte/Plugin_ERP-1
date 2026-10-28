/* شرح الملف: تحديد أنواع المتصفحات المتاحة.
 */
package Plugin_ERP.Drivers;

public enum Browser {
    CHROME {
        @Override
        public AbstractDriver getDriverFactory() {
            return new Chrome();
        }
    },

    EDGE {
        @Override
        public AbstractDriver getDriverFactory() {
            return new Edge();
        }
    },
    FIREFOX {
        @Override
        public AbstractDriver getDriverFactory() {
            return new Firefox();
        }
    };

    public abstract AbstractDriver getDriverFactory();
}
