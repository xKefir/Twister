package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;

import java.util.ArrayList;
import java.util.List;

public class TextFormatUtil {

    public static Component format(String input, TagResolver... resolvers) {
        if (Twister.get().getMessages().getString(MessageKey.MESSAGES_INPUT_TYPE.getPath()).equals("LEGACY")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(
                    LegacyComponentSerializer.legacyAmpersand().serialize(
                            MiniMessage.builder().build().deserialize(input, resolvers))).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return MiniMessage.miniMessage().deserialize(input, resolvers).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static List<Component> format(List<String> input, TagResolver... resolvers) {
        List<Component> formattedComponent = new ArrayList<>();
        for (String str : input) {
            Component formatted = format(str, resolvers); // Przekazuj resolvers do każdego elementu
            formattedComponent.add(formatted);
        }
        return formattedComponent;
    }

    public static Component fromMessages(MessageKey key, TagResolver... resolvers) {
        String message = Twister.get().getMessages().getString(key.getPath());
        if (message == null) {
            return Component.text("Missing message: " + key.getPath());
        }
        return format(message, resolvers);
    }

    public static List<Component> fromMessagesList(MessageKey key, TagResolver... resolvers) {
        List<String> messages = Twister.get().getMessages().getStringList(key.getPath());
        if (messages == null || messages.isEmpty()) {
            return List.of(Component.text("Missing message list: " + key.getPath()));
        }
        return format(messages, resolvers);
    }

    // HELPER METHODS - żeby nie powtarzać kodu

    public static TagResolver prefixPlaceholder() {
        return Placeholder.component("prefix", fromMessages(MessageKey.MESSAGES_PREFIX_STRING));
    }

    public static Component withPrefix(MessageKey key, TagResolver... additionalResolvers) {
        TagResolver[] resolvers = new TagResolver[additionalResolvers.length + 1];
        resolvers[0] = prefixPlaceholder();
        System.arraycopy(additionalResolvers, 0, resolvers, 1, additionalResolvers.length);
        return fromMessages(key, resolvers);
    }

    public static List<Component> withPrefixList(MessageKey key, TagResolver... additionalResolvers) {
        TagResolver[] resolvers = new TagResolver[additionalResolvers.length + 1];
        resolvers[0] = prefixPlaceholder();
        System.arraycopy(additionalResolvers, 0, resolvers, 1, additionalResolvers.length);
        return fromMessagesList(key, resolvers);
    }
}