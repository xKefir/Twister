package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.file.message.MessageProviderLoader;

import java.util.ArrayList;
import java.util.List;

public class TextFormatUtil {

    public static Component format(String input, TagResolver... resolvers) {
        if (MessageProviderLoader.getString(MessageKey.MESSAGES_INPUT_TYPE.getPath()).equals("LEGACY")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(
                    LegacyComponentSerializer.legacyAmpersand().serialize(
                            MiniMessage.builder().build().deserialize(input, resolvers))).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return MiniMessage.miniMessage().deserialize(input, resolvers).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
    public static List<Component> format(List<String> input, TagResolver... resolvers) {
        List<Component> formattedComponent = new ArrayList<>();
        input.forEach(str -> {
            Component formatted = format(str, resolvers);
            formattedComponent.add(formatted);
        });
        return formattedComponent;
    }


}
