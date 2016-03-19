package lx.af.utils.ViewInject;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * author: lx
 * date: 16-1-7
 */
public final class ViewInjectUtils {

    public static final String TAG = "ViewInject";

    private ViewInjectUtils() {}

    /**
     * user {@link ViewInject} annotation to assign View fields
     * @param target must has a "findViewById" method, or exception will throw
     */
    public static void inject(Object target) {
        injectInner(Object.class, target, target);
    }

    /**
     * user {@link ViewInject} annotation to assign View fields in param view
     * @param target owner of the view fields
     * @param view where the views are from
     */
    public static void inject(Object target, View view) {
        injectInner(Object.class, target, view);
    }

    /**
     * user {@link ViewInject} annotation to assign View fields
     * @param base base class for {@link ViewInject} to apply.
     *             will only find fields for inject on subclass of this class
     * @param target must has a "findViewById" method, or exception will throw
     */
    public static void inject(Class<?> base, Object target) {
        injectInner(base, target, target);
    }

    /**
     * user {@link ViewInject} annotation to assign View fields in param view
     * @param base base class for {@link ViewInject} to apply.
     *             will only find fields for inject on subclass of this class
     * @param target owner of the view fields
     * @param view where the views are from
     */
    public static void inject(Class<?> base, Object target, View view) {
        injectInner(base, target, view);
    }

    private static void injectInner(Class<?> base, Object injectTarget, Object viewSource) {
        List<FieldInfo> list = getFieldInfoList(base, injectTarget);

        for (FieldInfo info : list) {
            Field field = info.field;
            ViewInject inject = info.inject;

            if (inject != null) {
                int viewId = inject.id();
                View view = null;
                try {
                    field.setAccessible(true);
                    view = findViewById(viewSource, viewId);
                    if (view == null) {
                        throw new RuntimeException("inject source view null for (" + field + ")");
                    }
                    field.set(injectTarget, view);
                } catch (IllegalArgumentException | NoSuchMethodException e) {
                    String viewName = view != null ? view.getClass().getName() : null;
                    throw new RuntimeException(
                            "inject view fail for (" + viewName + ") to (" + field + ")", e);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String clickMethod = inject.click();
                if (!TextUtils.isEmpty(clickMethod))
                    setViewClickListener(base, injectTarget, field, clickMethod);

                String longClickMethod = inject.longClick();
                if (!TextUtils.isEmpty(longClickMethod))
                    setViewLongClickListener(base, injectTarget, field, longClickMethod);

                String itemClickMethod = inject.itemClick();
                if (!TextUtils.isEmpty(itemClickMethod))
                    setItemClickListener(base, injectTarget, field, itemClickMethod);

                String itemLongClickMethod = inject.itemLongClick();
                if (!TextUtils.isEmpty(itemLongClickMethod))
                    setItemLongClickListener(base, injectTarget, field, itemLongClickMethod);

                Select select = inject.select();
                if (!TextUtils.isEmpty(select.selected()))
                    setViewSelectListener(
                            base, injectTarget, field, select.selected(), select.noSelected());
            }
        }
    }

    private static List<FieldInfo> getFieldInfoList(Class<?> base, Object object) {
        LinkedList<FieldInfo> list = new LinkedList<>();
        Class<?> clazz = object.getClass() ;
        while (!clazz.equals(Object.class) && !clazz.equals(base)) {
            Field[] fs = clazz.getDeclaredFields();
            if (fs != null && fs.length != 0) {
                for (Field field : fs) {
                    ViewInject inject = field.getAnnotation(ViewInject.class);
                    if (inject != null) {
                        list.add(new FieldInfo(field, inject));
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    private static View findViewById(Object viewSource, int id)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] argTypes = new Class[1];
        argTypes[0] = Integer.TYPE;
        Method findViewById = viewSource.getClass().getMethod("findViewById", argTypes);
        Object[] args = new Object[1];
        args[0] = id;
        return (View) findViewById.invoke(viewSource, args);
    }


    private static void setViewClickListener(
            Class<?> base, Object target, Field field, String method) {
        try {
            Object obj = field.get(target);
            if (obj instanceof View) {
                ((View) obj).setOnClickListener(
                        new EventListener(base, target).click(method));
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to setOnClickListener", e);
        }
    }

    private static void setViewLongClickListener(
            Class<?> base, Object target, Field field, String method) {
        try {
            Object obj = field.get(target);
            if (obj instanceof View) {
                ((View) obj).setOnLongClickListener(
                        new EventListener(base, target).longClick(method));
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to setOnLongClickListener", e);
        }
    }

    private static void setItemClickListener(
            Class<?> base, Object target, Field field, String method) {
        try {
            Object obj = field.get(target);
            if (obj instanceof AbsListView) {
                ((AbsListView) obj).setOnItemClickListener(
                        new EventListener(base, target).itemClick(method));
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to setOnItemClickListener", e);
        }
    }

    private static void setItemLongClickListener(
            Class<?> base, Object target, Field field, String method) {
        try {
            Object obj = field.get(target);
            if (obj instanceof AbsListView) {
                ((AbsListView) obj).setOnItemLongClickListener(
                                new EventListener(base, target).itemLongClick(method));
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to setOnItemLongClickListener", e);
        }
    }

    private static void setViewSelectListener(
            Class<?> base, Object target, Field field, String select, String noSelect) {
        try {
            Object obj = field.get(target);
            if (obj instanceof View) {
                ((AbsListView) obj).setOnItemSelectedListener(
                        new EventListener(base, target).select(select).noSelect(noSelect));
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to setOnItemSelectedListener", e);
        }
    }

    private static class FieldInfo {
        Field field;
        ViewInject inject;

        public FieldInfo(Field field, ViewInject inject) {
            this.field = field;
            this.inject = inject;
        }
    }

}
