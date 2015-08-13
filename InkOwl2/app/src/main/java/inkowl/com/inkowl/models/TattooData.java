package inkowl.com.inkowl.models;

import android.content.Context;

public class TattooData
{
    private static TattooData instance;

    public static TattooData getInstance()
    {
        if (instance == null)
        {
            instance = new TattooData();
        }

        return instance;
    }

    private Context context;
    private DataManager dataManager;

    private TattooData()
    {

    }

    public void init(Context context)
    {
        this.context = context;
        dataManager = new DataManager(context);
    }

    public DataManager getDataManager()
    {
        return dataManager;
    }

}
