package com.jakewharton.u2020.ui;

import com.cocosw.xteam.ui.MainActivity;
import com.jakewharton.u2020.U2020Module;

import dagger.Module;

@Module(
    addsTo = U2020Module.class,
    injects = {MainActivity.class, MainActivity.MainFragment.class}
)
public final class MainActivityModule {
  private final MainActivity mainActivity;

  public MainActivityModule(MainActivity mainActivity) {
    this.mainActivity = mainActivity;
  }

}
