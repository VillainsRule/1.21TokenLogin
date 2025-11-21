package dev.majanito;

import dev.majanito.utils.SessionUtils;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionIDLoginMod implements ModInitializer {
    public static final String MOD_ID = "session-id-login-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Session originalSession;
    public static Session currentSession;
    public static boolean overrideSession = false;

    @Override
    public void onInitialize() {
        originalSession = SessionUtils.getSession();
        currentSession = originalSession;
        overrideSession = true;
    }
}
