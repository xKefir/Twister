package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProviderLoader;

public class TextFormatUtil {

    public static Component format(String input, TagResolver... resolvers) {
        if (MessageProviderLoader.getString(MessageKey.MESSAGES_INPUT_TYPE.getPath()).equals("LEGACY")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(
                    LegacyComponentSerializer.legacyAmpersand().serialize(
                            MiniMessage.builder().build().deserialize(input, resolvers))
            );
        }
        return MiniMessage.miniMessage().deserialize(input, resolvers);
    }
}
