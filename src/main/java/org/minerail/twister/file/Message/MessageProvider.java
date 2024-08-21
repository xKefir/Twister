package org.minerail.twister.file.Message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.minerail.twister.util.TextFormatUtil;


public class MessageProvider {

    public static Component get(MessageKey key, TagResolver... resolvers) {
        return TextFormatUtil.format(MessageProviderLoader.getString(key.getPath()), resolvers);
    }
}
