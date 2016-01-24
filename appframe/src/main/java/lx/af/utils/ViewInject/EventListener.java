package lx.af.utils.ViewInject;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import java.lang.reflect.Method;

import static lx.af.utils.ViewInject.ViewInjectUtils.TAG;

/**
 * modified by liuxu
 * date: 16-1-7
 */
class EventListener implements
        OnClickListener,
        OnLongClickListener,
        OnItemClickListener,
        OnItemSelectedListener,
        OnItemLongClickListener {

    private Class<?> base;
	private Object handler;
	
	private String clickMethod;
	private String longClickMethod;
	private String itemClickMethod;
	private String itemSelectMethod;
	private String itemLongClickMethod;
    private String nothingSelectedMethod;

	public EventListener(Class<?> base, Object handler) {
        this.base = base;
		this.handler = handler;
	}
	
	public EventListener click(String method) {
		this.clickMethod = method;
		return this;
	}
	
	public EventListener longClick(String method) {
		this.longClickMethod = method;
		return this;
	}

	public EventListener itemLongClick(String method) {
		this.itemLongClickMethod = method;
		return this;
	}
	
	public EventListener itemClick(String method) {
		this.itemClickMethod = method;
		return this;
	}
	
	public EventListener select(String method) {
		this.itemSelectMethod = method;
		return this;
	}
	
	public EventListener noSelect(String method) {
		this.nothingSelectedMethod = method;
		return this;
	}

    @Override
	public boolean onLongClick(View v) {
		return invokeLongClickMethod(base, handler, longClickMethod, v);
	}

    @Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		return invokeItemLongClickMethod(base, handler, itemLongClickMethod, arg0, arg1, arg2, arg3);
	}

    @Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		invokeItemSelectMethod(base, handler, itemSelectMethod, arg0, arg1, arg2, arg3);
	}

    @Override
	public void onNothingSelected(AdapterView<?> arg0) {
		invokeNoSelectMethod(base, handler, nothingSelectedMethod, arg0);
	}

    @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		invokeItemClickMethod(base, handler, itemClickMethod, arg0, arg1, arg2, arg3);
	}

    @Override
	public void onClick(View v) {
		invokeClickMethod(base, handler, clickMethod, v);
	}

	private static Object invokeClickMethod(
            Class<?> base, Object handler, String methodName, Object... params) {
		if (handler == null) return null;
		Method method = getMethod(base, handler, methodName, View.class);
        if (method != null) {
            try {
                return method.invoke(handler, params);
            } catch (Exception e) {
                throw new RuntimeException("invoke method " + methodName + " failed", e);
            }
        } else {
            throw new RuntimeException("no such method: " + methodName);
        }
	}

	private static boolean invokeLongClickMethod(
            Class<?> base, Object handler, String methodName, Object... params) {
        if (handler == null) return false;
        Method method = getMethod(base, handler, methodName, View.class);
        if (method != null) {
            try {
                Object obj = method.invoke(handler, params);
                return obj == null ? false : Boolean.valueOf(obj.toString());
            } catch (Exception e) {
                throw new RuntimeException("invoke method " + methodName + " failed", e);
            }
        } else {
            throw new RuntimeException("no such method: " + methodName);
        }
	}

	private static Object invokeItemClickMethod(
            Class<?> base, Object handler, String methodName, Object...params) {
        if (handler == null) return false;
        Method method = getMethod(
                base, handler, methodName, AdapterView.class, View.class, int.class, long.class);
        if (method != null) {
            try {
                return method.invoke(handler, params);
            } catch (Exception e) {
                throw new RuntimeException("invoke method " + methodName + " failed", e);
            }
        } else {
            throw new RuntimeException("no such method: " + methodName);
        }
	}

	private static boolean invokeItemLongClickMethod(
            Class<?> base, Object handler, String methodName, Object...params) {
        if (handler == null) return false;
        Method method = getMethod(
                base, handler, methodName, AdapterView.class, View.class, int.class, long.class);
        if (method != null) {
            try {
                Object obj = method.invoke(handler, params);
                return obj == null ? false : Boolean.valueOf(obj.toString());
            } catch (Exception e) {
                throw new RuntimeException("invoke method " + methodName + " failed", e);
            }
        } else {
            throw new RuntimeException("no such method: " + methodName);
        }
	}

	private static Object invokeItemSelectMethod(
            Class<?> base, Object handler, String methodName, Object...params) {
        if (handler == null) return false;
        Method method = getMethod(
                base, handler, methodName, AdapterView.class, View.class, int.class, long.class);
        if (method != null) {
            try {
                return method.invoke(handler, params);
            } catch (Exception e) {
                throw new RuntimeException("invoke method " + methodName + " failed", e);
            }
        } else {
            throw new RuntimeException("no such method: " + methodName);
        }
	}
	
	private static Object invokeNoSelectMethod(
            Class<?> base, Object handler, String methodName, Object... params) {
        if (handler == null) return false;
        ///onNothingSelected(AdapterView<?> arg0)
        Method method = getMethod(base, handler, methodName, AdapterView.class);
        if (method != null) {
            try {
                return method.invoke(handler, params);
            } catch (Exception e) {
                throw new RuntimeException("invoke method " + methodName + " failed", e);
            }
        } else {
            throw new RuntimeException("no such method: " + methodName);
        }
	}


    private static Method getMethod(
            Class<?> base, Object handler, String methodName, Class<?>... params) {

        Method method = null;
        Class<?> clazz = handler.getClass();
        while (!clazz.equals(Object.class) && !clazz.equals(base)) {
            try {
                method = clazz.getDeclaredMethod(methodName, params);
                if (method != null) {
                    break;
                }
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "find method, no " + methodName + " in " + clazz.getSimpleName());
            }
            clazz = clazz.getSuperclass();
        }
        return method;
    }

}
