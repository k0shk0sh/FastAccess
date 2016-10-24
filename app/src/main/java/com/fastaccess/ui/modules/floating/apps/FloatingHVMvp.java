package com.fastaccess.ui.modules.floating.apps;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.ui.modules.floating.BaseFloatingMvp;

/**
 * Created by Kosh on 14 Oct 2016, 8:54 PM
 */

public interface FloatingHVMvp {

    interface Presenter extends BaseFloatingMvp.BasePresenter<AppsModel, BaseFloatingMvp.BaseView<AppsModel>> {}
}
