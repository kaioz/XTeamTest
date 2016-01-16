package com.jakewharton.u2020;

import com.cocosw.xteam.WarehouseApp;
import com.jakewharton.u2020.DebugU2020Module;
import com.jakewharton.u2020.U2020Module;

public final class Modules {
  public static Object[] list(WarehouseApp app) {
    return new Object[] {
        new U2020Module(app),
        new DebugU2020Module()
    };
  }

  private Modules() {
    // No instances.
  }
}
