package com.fastaccess.provider.icon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.ThemePackEventModel;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.provider.icon.model.IconPackInfo;

import org.greenrobot.eventbus.EventBus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconPackHelper {
    public final static int PICK_ICON = 2001;
    static final String ICON_MASK_TAG = "iconmask";
    static final String ICON_BACK_TAG = "iconback";
    static final String ICON_UPON_TAG = "iconupon";
    static final String ICON_SCALE_TAG = "scale";
    final static String PLAY_STORE_PACKAGENAME = "com.android.vending";
    final static String PLAY_STORE_SEARCH_URI = "market://search?q=icon+pack";

    public final static String[] sSupportedActions = new String[]{
            "org.adw.launcher.THEMES",
            "com.gau.go.launcherex.theme"
    };

    public static final String[] sSupportedCategories = new String[]{
            "com.fede.launcher.THEME_ICONPACK",
            "com.anddoes.launcher.THEME",
            "com.teslacoilsw.launcher.THEME"
    };

    private Map<String, String> mIconPackResources;
    private final Context mContext;
    private String mLoadedIconPackName;
    private Resources mLoadedIconPackResource;
    private Drawable mIconBack, mIconUpon, mIconMask;
    private float mIconScale;

    public Drawable getIconBack() {
        return mIconBack;
    }

    public Drawable getIconMask() {
        return mIconMask;
    }

    public Drawable getIconUpon() {
        return mIconUpon;
    }

    public float getIconScale() {
        return mIconScale;
    }

    public IconPackHelper(Context context) {
        mContext = context;
        mIconPackResources = new HashMap<>();
    }

    private Drawable getDrawableForName(String name) {
        if (isIconPackLoaded()) {
            String item = mIconPackResources.get(name);
            if (!TextUtils.isEmpty(item)) {
                int id = getResourceIdForDrawable(item);
                if (id != 0) {
                    return mLoadedIconPackResource.getDrawable(id);
                }
            }
        }
        return null;
    }

    public static Map<String, IconPackInfo> getSupportedPackages(Context context) {
        Intent i = new Intent();
        Map<String, IconPackInfo> packages = new HashMap<>();
        PackageManager packageManager = context.getPackageManager();
        for (String action : sSupportedActions) {
            i.setAction(action);
            for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
                IconPackInfo info = new IconPackInfo(r, packageManager);
                packages.put(r.activityInfo.packageName, info);
            }
        }
        i = new Intent(Intent.ACTION_MAIN);
        for (String category : sSupportedCategories) {
            i.addCategory(category);
            for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
                IconPackInfo info = new IconPackInfo(r, packageManager);
                packages.put(r.activityInfo.packageName, info);
            }
            i.removeCategory(category);
        }
        return packages;
    }

    private static void loadResourcesFromXmlParser(XmlPullParser parser, Map<String, String> iconPackResources) throws XmlPullParserException,
            IOException {
        int eventType = parser.getEventType();
        do {

            if (eventType != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equalsIgnoreCase(ICON_MASK_TAG) ||
                    parser.getName().equalsIgnoreCase(ICON_BACK_TAG) ||
                    parser.getName().equalsIgnoreCase(ICON_UPON_TAG)) {
                List<String> icons = new ArrayList<>();
                icons.add(parser.getAttributeValue(null, "img"));
                for (int i = 0; i < 10; i++) {
                    icons.add(parser.getAttributeValue(null, "img" + i));
                }
                for (String icon : icons) {
                    if (icon == null) {
                        if (parser.getAttributeCount() > 0) {
                            for (int count = 0; count < parser.getAttributeCount(); count++) {
                                icon = parser.getAttributeValue(count);
                                iconPackResources.put(parser.getName().toLowerCase(), icon);
                            }
                        }
                    }
                }
                continue;
            }
            if (parser.getName().equalsIgnoreCase(ICON_SCALE_TAG)) {
                String factor = parser.getAttributeValue(null, "factor");
                if (factor == null) {
                    if (parser.getAttributeCount() == 1) {
                        factor = parser.getAttributeValue(0);
                    }
                }
                iconPackResources.put(parser.getName().toLowerCase(), factor);
                continue;
            }

            if (!parser.getName().equalsIgnoreCase("item")) {
                continue;
            }

            String component = parser.getAttributeValue(null, "component");
            String drawable = parser.getAttributeValue(null, "drawable");

            // Validate component/drawable exist
            if (TextUtils.isEmpty(component) || TextUtils.isEmpty(drawable)) {
                continue;
            }

            // Validate format/length of component
            if (!component.startsWith("ComponentInfo{") || !component.endsWith("}")
                    || component.length() < 16) {
                continue;
            }

            // Sanitize stored value
            component = component.substring(14, component.length() - 1).toLowerCase();

            ComponentName name = null;
            if (!component.contains("/")) {
                // Package icon reference
                iconPackResources.put(component, drawable);
            } else {
                name = ComponentName.unflattenFromString(component);
                if (name != null) {
                    iconPackResources.put(name.getPackageName(), drawable);
                    iconPackResources.put(name.getPackageName() + "." + name.getClassName(), drawable);
                }
            }
        } while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT);
    }

    private static void loadApplicationResources(Context context, Map<String, String> iconPackResources, String packageName) {
        Field[] drawableItems = null;
        try {
            Context appContext = context.createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            drawableItems = Class.forName(packageName + ".R$drawable",
                    true, appContext.getClassLoader()).getFields();
        } catch (Exception e) {
            return;
        }
        for (Field f : drawableItems) {
            String name = f.getName();

            String icon = name.toLowerCase();
            name = name.replaceAll("_", ".");

            iconPackResources.put(name, icon);

            int activityIndex = name.lastIndexOf(".");
            if (activityIndex <= 0 || activityIndex == name.length() - 1) {
                continue;
            }

            String iconPackage = name.substring(0, activityIndex);
            if (TextUtils.isEmpty(iconPackage)) {
                continue;
            }
            iconPackResources.put(iconPackage, icon);

            String iconActivity = name.substring(activityIndex + 1);
            if (TextUtils.isEmpty(iconActivity)) {
                continue;
            }
            iconPackResources.put(iconPackage + "." + iconActivity, icon);
        }
    }

    public boolean loadIconPack(String packageName) {
        mIconPackResources = getIconPackResources(mContext, packageName);
        Resources res;
        try {
            res = mContext.getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mLoadedIconPackResource = res;
        mLoadedIconPackName = packageName;
        mIconBack = getDrawableForName(ICON_BACK_TAG);
        mIconMask = getDrawableForName(ICON_MASK_TAG);
        mIconUpon = getDrawableForName(ICON_UPON_TAG);
        String scale = mIconPackResources.get(ICON_SCALE_TAG);
        if (scale != null) {
            try {
                mIconScale = Float.valueOf(scale);
            } catch (NumberFormatException ignored) {}
        }
        return true;
    }

    public static Map<String, String> getIconPackResources(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }

        Resources res = null;
        try {
            res = context.getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        XmlPullParser parser = null;
        InputStream inputStream = null;
        Map<String, String> iconPackResources = new HashMap<String, String>();

        try {
            inputStream = res.getAssets().open("appfilter.xml");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");
        } catch (Exception e) {
            // Catch any exception since we want to fall back to parsing the xml/
            // resource in all cases
            int resId = res.getIdentifier("appfilter", "xml", packageName);
            if (resId != 0) {
                parser = res.getXml(resId);
            }
        }

        if (parser != null) {
            try {
                loadResourcesFromXmlParser(parser, iconPackResources);
                return iconPackResources;
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            } finally {
                // Cleanup resources
                if (parser instanceof XmlResourceParser) {
                    ((XmlResourceParser) parser).close();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {}
                }
            }
        }

        // Application uses a different theme format (most likely launcher pro)
        int arrayId = res.getIdentifier("theme_iconpack", "array", packageName);
        if (arrayId == 0) {
            arrayId = res.getIdentifier("icon_pack", "array", packageName);
        }

        if (arrayId != 0) {
            String[] iconPack = res.getStringArray(arrayId);
            for (String entry : iconPack) {

                if (TextUtils.isEmpty(entry)) {
                    continue;
                }

                String icon = entry.toLowerCase();
                entry = entry.replaceAll("_", ".");

                iconPackResources.put(entry, icon);

                int activityIndex = entry.lastIndexOf(".");
                if (activityIndex <= 0 || activityIndex == entry.length() - 1) {
                    continue;
                }

                String iconPackage = entry.substring(0, activityIndex);
                if (TextUtils.isEmpty(iconPackage)) {
                    continue;
                }
                iconPackResources.put(iconPackage, icon);

                String iconActivity = entry.substring(activityIndex + 1);
                if (TextUtils.isEmpty(iconActivity)) {
                    continue;
                }
                iconPackResources.put(iconPackage + "." + iconActivity, icon);
            }
        } else {
            loadApplicationResources(context, iconPackResources, packageName);
        }
        return iconPackResources;
    }

    public void unloadIconPack() {
        mLoadedIconPackResource = null;
        mLoadedIconPackName = null;
        mIconPackResources = null;
        mIconMask = null;
        mIconBack = null;
        mIconUpon = null;
        mIconScale = 1f;
    }

    public static void pickIconPack(final Fragment context, final boolean pickIcon) {
        Map<String, IconPackInfo> supportedPackages = getSupportedPackages(context.getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext(), R.style.AlertDialogStyle);
        if (supportedPackages.isEmpty()) {
            builder.setTitle(context.getString(R.string.get_themes));
            builder.setMessage(R.string.no_icon_packs)
                    .setPositiveButton(R.string.get_themes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(PLAY_STORE_SEARCH_URI));
                            context.startActivity(intent);
                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
            return;
        }
        final IconPackAdapter adapter = new IconPackAdapter(context.getContext(), supportedPackages, pickIcon);
        if (!pickIcon) {
            builder.setTitle(R.string.choose_theme_pack);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int position) {
                    if (adapter.isCurrentIconPack(position)) {
                        return;
                    }
                    String selectedPackage = adapter.getItem(position);
                    PrefHelper.set(PrefConstant.ICON_PACK, selectedPackage);
                    App.getInstance().flushIconPack();
                    EventBus.getDefault().post(new ThemePackEventModel());
                }
            }).setPositiveButton(context.getString(R.string.get_themes), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(PLAY_STORE_SEARCH_URI));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            builder.setTitle(R.string.choose_icon);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent intent = new Intent();
                        intent.putExtra("default", true);
                        context.onActivityResult(PICK_ICON, Activity.RESULT_CANCELED, intent);
                    } else {
                        String selectedPackage = adapter.getItem(which);
                        try {
                            Intent i = new Intent();
                            i.setPackage(selectedPackage);
                            i.setAction("org.adw.launcher.icons.ACTION_PICK_ICON");
                            i.putExtra("package", selectedPackage);
                            context.startActivityForResult(i, PICK_ICON);
                        } catch (Exception e) {
                            Toast.makeText(context.getContext(), context.getString(R.string.not_support_icon_theme), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
        builder.show();
    }

    boolean isIconPackLoaded() {
        return mLoadedIconPackResource != null &&
                mLoadedIconPackName != null &&
                mIconPackResources != null;
    }

    private int getResourceIdForDrawable(String resource) {
        return mLoadedIconPackResource.getIdentifier(resource, "drawable", mLoadedIconPackName);
    }

    public Resources getIconPackResources() {
        return mLoadedIconPackResource;
    }

    public int getResourceIdForActivityIcon(ActivityInfo info) {
        String drawable = mIconPackResources.get(info.packageName.toLowerCase()
                + "." + info.name.toLowerCase());
        if (drawable == null) {
            // Icon pack doesn't have an icon for the activity, fallback to package icon
            drawable = mIconPackResources.get(info.packageName.toLowerCase());
            if (drawable == null) {
                return 0;
            }
        }
        return getResourceIdForDrawable(drawable);
    }


}
