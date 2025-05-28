package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.minerail.twister.Twister;
import org.minerail.twister.file.leaderboard.Leaderboard;
import org.minerail.twister.file.message.MessageKey;

import java.util.ArrayList;
import java.util.List;

public class TextFormatUtil {

    public static Component format(String input, TagResolver... resolvers) {
        if (Twister.getMessages().getString(MessageKey.MESSAGES_INPUT_TYPE.getPath()).equals("LEGACY")) {
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

    public static List<Component> formatTopList(List<Leaderboard.TopEntry> entries) {
        List<Component> components = new ArrayList<>();
        int pos = 1;

        for (Leaderboard.TopEntry entry : entries) {
            components.add(format(
                    Twister.getMessages().getString(MessageKey.MESSAGES_COMMAND_TOP_LISTLINE.getPath()),
                    prefixPlaceholder(),
                    Placeholder.component("pos", Component.text(pos)),
                    Placeholder.component("player", Component.text(entry.getPlayerName())),
                    Placeholder.component("value", Component.text(entry.getValue()))
            ));
            if (pos == entries.size()) break;
            pos++;
        }

        return components;
    }

    public static Component fromMessages(MessageKey key, TagResolver... resolvers) {
        return format(Twister.getMessages().getString(key.getPath()), resolvers);
    }

    public static List<Component> fromMessagesList(MessageKey key, TagResolver... resolvers) {
        return format(Twister.getMessages().getStringList(key.getPath()), resolvers);
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