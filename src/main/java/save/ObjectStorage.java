package save;

import android.content.Context;

import com.google.gson.Gson;

public class ObjectStorage {

    public static void save(Object src, final String key, final Context Appcontext) {
        final String json = new Gson().toJson(src);
        new CachePref(Appcontext).put(key, json);


    }

    public static String getString(Object src) {
        return new Gson().toJson(src);
    }
    public static <T> T getData(String data, Class<T> klazz) {
        if (data.equals("")) {
            return null;
        }
        try {
            return new Gson().fromJson(data, klazz);

        }
        catch (IllegalStateException e){
            e.printStackTrace();
        }
        catch (com.google.gson.JsonSyntaxException e){
            e.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T get(String key, Class<T> klazz, Context Appcontext) {
        String jsonStr = new CachePref(Appcontext).get(key, "");
        if (jsonStr.equals("")) {
            return null;
        }
        try {
            return new Gson().fromJson(jsonStr, klazz);

        }
        catch (IllegalStateException e){
            e.printStackTrace();
        }
        catch (com.google.gson.JsonSyntaxException e){
            e.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
    public static void clear(String key, Context Appcontext){
        new CachePref(Appcontext).clear(key);
    }

}