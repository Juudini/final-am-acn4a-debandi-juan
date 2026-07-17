package com.example.final_am_acn4a_debandi_juan;
import android.app.Application;
import com.example.final_am_acn4a_debandi_juan.di.AppModule;

public class App extends Application {
    private AppModule appModule;
    @Override
    public void onCreate() {
        super.onCreate();
        appModule = new AppModule();
    }

    public AppModule getAppModule() {
        return appModule;
    }

    public static AppModule getModule(android.content.Context context) {
        return ((App) context.getApplicationContext()).getAppModule();
    }
}
