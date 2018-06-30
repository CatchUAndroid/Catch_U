package uren.com.catchu.SingletonClassPackages;

import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetAccountHolder;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetFriends;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetGroups;

public class ClearSingletonClasses {

    public static void clearAllClasses(){
        FirebaseGetAccountHolder.setInstance(null);
        FirebaseGetFriends.setInstance(null);
        FirebaseGetGroups.setInstance(null);
    }
}
