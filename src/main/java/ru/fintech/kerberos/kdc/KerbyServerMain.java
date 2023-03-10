package ru.fintech.kerberos.kdc;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.util.Arrays.asList;
import static org.apache.kerby.kerberos.kerb.server.KdcConfigKey.PREAUTH_REQUIRED;

/**
 * Simple KDC which utilizes Apache Kerby.
 */
public class KerbyServerMain {

  private static final Logger log = LoggerFactory.getLogger(KerbyServerMain.class);

  public static void main(String[] args) throws KrbException {
    SimpleKdcServer kdc = new SimpleKdcServer();
    // kdc.enableDebug();
    kdc.setKdcHost("localhost");
    kdc.setKdcRealm("TEST.REALM");
    kdc.setKdcPort(10088);
    kdc.setAllowUdp(false);
    kdc.getKdcConfig().setBoolean(PREAUTH_REQUIRED, false);
    // SimpleKdcServer init also creates krb5.conf file and initializes Kadmin API.
    kdc.init();

    // user    principal name: principal-name[/instance-name]@REALM
    kdc.createPrincipal("jduke", "theduke");
    kdc.createPrincipal("hnelson", "secret");

    kdc.createPrincipal("gsstest/localhost", "servicePassword");
    kdc.createPrincipal("hazelcast/localhost", "s1mpl3+FAST");

    // export service principal's keytab
    File keytabFile = new File("service.keytab");
    if (!keytabFile.exists()) {
      // service principal name: service-name/QDN@REALM
      kdc.getKadmin().exportKeytab(keytabFile,
          asList("gsstest/localhost@TEST.REALM", "hazelcast/localhost@TEST.REALM"));
    }

    kdc.start();
    log.info("Kerberos server has started.");
//    System.out.println("Kerberos server has started.");
  }
}
