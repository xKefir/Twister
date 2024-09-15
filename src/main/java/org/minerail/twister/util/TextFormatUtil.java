package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProviderLoader;

import java.util.ArrayList;
import java.util.List;

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
    public static List<Component> format(List<String> input, TagResolver... resolvers) {
        List<Component> formattedComponent = new ArrayList<>();
        input.forEach(str -> {
            Component formatted = format(str, resolvers);
            formattedComponent.add(formatted);
        });
        return formattedComponent;
    }

    public static List<String> serialize(List<Component> input) {
        List<String> serializedText = new ArrayList<>();
        for (Component text : input) {
            serializedText.add(serialize(text));
        }
        return serializedText;
    }

    public static String serialize(Component input) {
        return GsonComponentSerializer.gson().serialize(input);
    }
}
