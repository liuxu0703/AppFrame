package lx.af.utils.ViewInject;

import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: lx
 * date: 16-1-7
 */
public final class ViewInjectUtils {

    private ViewInjectUtils() {}

    /**
     * user {@link ViewInject} annotation to assign View fields
     * @param target must has a "findViewById" method, or exception will throw
     */
    public static void inject(Object target) {
        injectInner(target, target);
    }

    /**
     * user {@link ViewInject} annotation to assign View fields in param view
     * @param target owner of the view fields
     * @param view where the views are from
     */
    public static void inject(Object target, View view) {
        injectInner(target, view);
    }

    private static void injectInner(Object injectTarget, Object viewSource) {
        Field[] fields = injectTarget.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    int viewId = viewInject.id();
                    try {
                        field.setAccessible(true);
                        field.set(injectTarget, findViewById(viewSource, viewId));
                    } catch (NoSuchMethodException nme) {
                        throw new RuntimeException(nme);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String clickMethod = viewInject.click();
                    if (!TextUtils.isEmpty(clickMethod))
                        setViewClickListener(injectTarget, field, clickMethod);

                    String longClickMethod = viewInject.longClick();
                    if (!TextUtils.isEmpty(longClickMethod))
                        setViewLongClickListener(injectTarget, field, longClickMethod);

                    String itemClickMethod = viewInject.itemClick();
                    if (!TextUtils.isEmpty(itemClickMethod))
                        setItemClickListener(injectTarget, field, itemClickMethod);

                    String itemLongClickMethod = viewInject.itemLongClick();
                    if (!TextUtils.isEmpty(itemLongClickMethod))
                        setItemLongClickListener(injectTarget, field, itemLongClickMethod);

                    Select select = viewInject.select();
                    if (!TextUtils.isEmpty(select.selected()))
                        setViewSelectListener(
                                injectTarget, field, select.selected(), select.noSelected());
                }
            }
        }
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

    private static void setViewClickListener(Object target, Field field, String clickMethod) {
        try {
            Object obj = field.get(target);
            if (obj instanceof View) {
                ((View) obj).setOnClickListener(new EventListener(target)
                        .click(clickMethod));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setViewLongClickListener(Object target, Field field, String clickMethod) {
        try {
            Object obj = field.get(target);
            if (obj instanceof View) {
                ((View) obj).setOnLongClickListener(new EventListener(target)
                        .longClick(clickMethod));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setItemClickListener(Object target, Field field, String itemClickMethod) {
        try {
            Object obj = field.get(target);
            if (obj instanceof AbsListView) {
                ((AbsListView) obj).setOnItemClickListener(new EventListener(
                        target).itemClick(itemClickMethod));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setItemLongClickListener(Object target, Field field, String itemClickMethod) {
        try {
            Object obj = field.get(target);
            if (obj instanceof AbsListView) {
                ((AbsListView) obj)
                        .setOnItemLongClickListener(new EventListener(target)
                                .itemLongClick(itemClickMethod));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setViewSelectListener(
            Object target, Field field, String select, String noSelect) {
        try {
            Object obj = field.get(target);
            if (obj instanceof View) {
                ((AbsListView) obj)
                        .setOnItemSelectedListener(new EventListener(target)
                                .select(select).noSelect(noSelect));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
