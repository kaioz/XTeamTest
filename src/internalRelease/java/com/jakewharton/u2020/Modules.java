package com.jakewharton.u2020;

import com.cocosw.xteam.WarehouseApp;

public final class Modules {
  public static Object[] list(WarehouseApp app) {
    return new Object[] {
        new U2020Module(app),
        new InternalReleaseU2020Module()
    };
  }

  private Modules() {
    // No instances.
  }
}
