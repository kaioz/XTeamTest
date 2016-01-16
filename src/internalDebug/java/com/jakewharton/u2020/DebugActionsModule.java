package com.jakewharton.u2020;

import com.jakewharton.u2020.ui.debug.ContextualDebugActions.DebugAction;
import dagger.Module;
import dagger.Provides;
import java.util.LinkedHashSet;
import java.util.Set;

import static dagger.Provides.Type.SET_VALUES;

@Module(complete = false, library = true) public final class DebugActionsModule {
  @Provides(type = SET_VALUES) Set<DebugAction> provideDebugActions() {
    Set<DebugAction> actions = new LinkedHashSet<>();
//    actions.add(scrollTopTrendingAction);
//    actions.add(scrollBottomTrendingAction);
    return actions;
  }
}
