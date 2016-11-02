package ch.hsr.mge.gadgeothek.service;

import java.util.HashMap;

import ch.hsr.mge.gadgeothek.R;

/**
 * Implement IDecoratorService with a local HashMap as a Singleton.
 */
public class LocalDecoratorService implements IDecoratorService {

    private HashMap<String,Integer> KnownLogos;
    private static IDecoratorService service;

    public static IDecoratorService getDecoratorService(){
        if (service==null){
            service = new LocalDecoratorService();
        }
        return service;
    }

    private LocalDecoratorService(){
        KnownLogos = new HashMap<String,Integer>();
        KnownLogos.put("UNKOWN", R.mipmap.ic_unkown);
        KnownLogos.put("apple", R.mipmap.ic_apple);
        KnownLogos.put("htc", R.mipmap.ic_htc);
        KnownLogos.put("samsung", R.mipmap.ic_samsung);
    }

    @Override
    public int getDrawableIdForManufacturerName(String manufacturer) {
        manufacturer = manufacturer.toLowerCase();
        if(KnownLogos.containsKey(manufacturer)) {
            return KnownLogos.get(manufacturer);
        }else{
            return KnownLogos.get("UNKOWN");
        }
    }
}
