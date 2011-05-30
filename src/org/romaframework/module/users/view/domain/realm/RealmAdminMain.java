package org.romaframework.module.users.view.domain.realm;

import java.security.NoSuchAlgorithmException;

import org.romaframework.module.admin.domain.Realm;
import org.romaframework.module.admin.view.domain.realm.RealmListable;
import org.romaframework.module.admin.view.domain.realm.RealmMain;
import org.romaframework.module.users.install.UsersApplicationInstaller;

public class RealmAdminMain extends RealmMain {
  public void createAdmin() throws NoSuchAlgorithmException {
    if (getSelection() != null && getSelection().length > 0) {
      Realm selRealm = ((RealmListable) getSelection()[0]).getEntity();

      UsersApplicationInstaller installer = new UsersApplicationInstaller();
      installer.install(selRealm);
    }
  }
}
