package dev._2lstudios.koth.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev._2lstudios.koth.KothPlugin;

public class ReflectionUtil {
	private static final Map<String, Class<?>> classes = new HashMap<>();
	private static final String version = Bukkit.class.getPackage().getName().split("\\.")[3];

	public static Class<?> getClass(final String className) throws ClassNotFoundException {
		if (classes.containsKey(className)) {
			return classes.get(className);
		}

		final Class<?> craftBukkitClass = Class.forName(className);

		classes.put(className, craftBukkitClass);

		return craftBukkitClass;
	}

	public static Object getField(final Object object, final Class<?> fieldType) throws IllegalAccessException {
		if (object == null) {
			throw new IllegalAccessException("Tried to access field from a null object");
		}

		for (final Field field : object.getClass().getFields()) {
			if (field.getType().equals(fieldType)) {
				final boolean accessible = field.isAccessible();

				field.setAccessible(true);

				final Object value = field.get(object);

				field.setAccessible(accessible);

				return value;
			}
		}

		return null;
	}

	private static Class<?> getNewNMClass(String key) {
		try {
			return getClass("net.minecraft." + key);
		} catch (final ClassNotFoundException e) {
			/* Ignored */
		}

		return null;
	}

	private static Class<?> getNetMinecraftClass(String key) {
		try {
			final int lastDot = key.lastIndexOf(".");
			final String lastKey = key.substring(lastDot > 0 ? lastDot + 1 : 0, key.length());

			return getClass("net.minecraft.server." + version + "." + lastKey);
		} catch (final ClassNotFoundException e) {
			/* Ignored */
		}

		return getNewNMClass(key);
	}

	private static Class<?> getCraftBukkitClass(String key) {
		try {
			getClass("org.bukkit.craftbukkit." + version + "." + key);
		} catch (final ClassNotFoundException e) {
			/* Ignored */
		}

		return null;
	}

	public static Class<?> getItemStack() {
		return getNetMinecraftClass("world.item.ItemStack");
	}

	public static Class<?> getMinecraftKey() {
		return getNetMinecraftClass("resources.MinecraftKey");
	}

	public static Class<?> getEnumProtocol() {
		return getNetMinecraftClass("network.EnumProtocol");
	}

	public static Class<?> getEnumProtocolDirection() {
		return getNetMinecraftClass("network.protocol.EnumProtocolDirection");
	}

	public static Class<?> getNetworkManager() {
		return getNetMinecraftClass("network.NetworkManager");
	}

	public static Class<?> getPacketDataSerializer() {
		return getNetMinecraftClass("network.PacketDataSerializer");
	}

	public static Class<?> getPacket() {
		return getNetMinecraftClass("network.protocol.Packet");
	}

	public static Class<?> getIChatBaseComponent() {
		return getNetMinecraftClass("network.chat.IChatBaseComponent");
	}

	public static Class<?> getPacketPlayOutKickDisconnect() {
		return getNetMinecraftClass("network.protocol.game.PacketPlayOutKickDisconnect");
	}

	public static Class<?> getPacketPlayOutTitle() {
		return getNetMinecraftClass("network.protocol.game.PacketPlayOutTitle");
	}

	public static Class<?> getPacketPlayOutChat() {
		return getNetMinecraftClass("network.protocol.game.PacketPlayOutChat");
	}

	public static Class<?> getPlayerConnection() {
		return getNetMinecraftClass("server.network.PlayerConnection");
	}

	public static Class<?> getClientboundSetTitlesAnimationPacket() {
		return getNetMinecraftClass("network.protocol.game.ClientboundSetTitlesAnimationPacket");
	}

	public static Class<?> getClientboundSetTitleTextPacket() {
		return getNetMinecraftClass("network.protocol.game.ClientboundSetTitleTextPacket");
	}

	public static Class<?> getClientboundSetSubtitleTextPacket() {
		return getNetMinecraftClass("network.protocol.game.ClientboundSetSubtitleTextPacket");
	}

	public static Class<?> getChatMessageType() {
		return getNetMinecraftClass("network.chat.ChatMessageType");
	}

	public static Class<?> getCraftItemStack() {
		return getCraftBukkitClass("inventory.CraftItemStack");
	}

	public static void sendPacket(final Player player, final Object packet) {
		try {
			final Object handler = player.getClass().getDeclaredMethod("getHandle").invoke(player);
			final Object playerConnection = getField(handler, getPlayerConnection());

			getPlayerConnection().getDeclaredMethod("sendPacket", getPacket()).invoke(playerConnection, packet);
		} catch (final Exception e) {
			KothPlugin.getInstance().getLogger().info("Failed to send packet to player " + player.getName() + "!");
		}
	}

	public static void sendActionbarPacketOld(final Player player, final String text)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchMethodException, SecurityException {
		final Method toChatBaseComponent = getIChatBaseComponent().getDeclaredClasses()[0].getDeclaredMethod("a",
				String.class);
		final Class<?> iChatBaseComponentClass = getIChatBaseComponent();
		final Object chatAction = toChatBaseComponent.invoke(null, "{ \"text\":\"" + text + "\" }");
		final Object packet = getPacketPlayOutChat().getConstructor(iChatBaseComponentClass, byte.class)
				.newInstance(chatAction, (byte) 2);

		sendPacket(player, packet);
	}

	public static void sendActionbarPacketNew(final Player player, final String text)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchMethodException, SecurityException {
		final Class<?> iChatBaseComponentClass = getIChatBaseComponent();
		final Method toChatBaseComponent = getIChatBaseComponent().getDeclaredClasses()[0].getDeclaredMethod("a",
				String.class);
		final Object chatAction = toChatBaseComponent.invoke(null, "{ \"text\":\"" + text + "\" }");
		final Class<?> chatMessageTypeClass = getChatMessageType();
		final Object[] enumConstants = chatMessageTypeClass.getEnumConstants();
		final Object packet = getPacketPlayOutChat()
				.getConstructor(iChatBaseComponentClass, chatMessageTypeClass, UUID.class)
				.newInstance(chatAction, enumConstants[2], player.getUniqueId());

		sendPacket(player, packet);
	}

	public static void sendActionbar(final Player player, final String text) {
		try {
			sendActionbarPacketNew(player, text);
		} catch (final Exception e1) {
			try {
				sendActionbarPacketOld(player, text);
			} catch (final Exception e2) {
				KothPlugin.getInstance().getLogger()
						.info("Failed to send actionbar packet to player " + player.getName() + "!");
			}
		}
	}

	public static void sendTitlePacketOld(final Player player, final String title, final String subtitle,
			final int fadeInTime, final int showTime, final int fadeOutTime)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, InstantiationException, NoSuchFieldException {
		final Class<?> iChatBaseComponentClass = getIChatBaseComponent();
		final Method toChatBaseComponent = getIChatBaseComponent().getDeclaredClasses()[0].getDeclaredMethod("a",
				String.class);
		final Object chatTitle = toChatBaseComponent.invoke(null, "{ \"text\":\"" + title + "\" }");
		final Object chatSubTitle = toChatBaseComponent.invoke(null, "{ \"text\":\"" + subtitle + "\" }");
		final Class<?> enumTitleActionClass = getPacketPlayOutTitle().getDeclaredClasses()[0];
		final Constructor<?> titleConstructor = getPacketPlayOutTitle().getConstructor(enumTitleActionClass,
				iChatBaseComponentClass, int.class, int.class, int.class);
		final Object titlePacket = titleConstructor.newInstance(
				enumTitleActionClass.getDeclaredField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);
		final Object subtitlePacket = titleConstructor.newInstance(
				enumTitleActionClass.getDeclaredField("SUBTITLE").get(null), chatSubTitle, fadeInTime, showTime,
				fadeOutTime);

		sendPacket(player, titlePacket);
		sendPacket(player, subtitlePacket);
	}

	public static void sendTitlePacketNew(final Player player, final String title, final String subtitle,
			final int fadeInTime, final int showTime, final int fadeOutTime)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, InstantiationException, NoSuchFieldException {
		final Class<?> iChatBaseComponentClass = getIChatBaseComponent();
		final Constructor<?> timingTitleConstructor = getClientboundSetTitlesAnimationPacket().getConstructor(int.class,
				int.class, int.class);
		final Object timingPacket = timingTitleConstructor.newInstance(fadeInTime, showTime, fadeOutTime);
		final Method toChatBaseComponent = getIChatBaseComponent().getDeclaredClasses()[0].getDeclaredMethod("a",
				String.class);
		final Object chatTitle = toChatBaseComponent.invoke(null, "{ \"text\":\"" + title + "\" }");
		final Constructor<?> titleConstructor = getClientboundSetTitleTextPacket()
				.getConstructor(iChatBaseComponentClass);
		final Object titlePacket = titleConstructor.newInstance(chatTitle);

		final Object chatSubTitle = toChatBaseComponent.invoke(null, "{ \"text\":\"" + subtitle + "\" }");
		final Constructor<?> subTitleConstructor = getClientboundSetSubtitleTextPacket()
				.getConstructor(iChatBaseComponentClass);
		final Object subTitlePacket = subTitleConstructor.newInstance(chatSubTitle);

		sendPacket(player, timingPacket);
		sendPacket(player, titlePacket);
		sendPacket(player, subTitlePacket);
	}

	public static void sendTitle(final Player player, final String title, final String subtitle, final int fadeInTime,
			final int showTime, final int fadeOutTime) {
		try {
			sendTitlePacketNew(player, title, subtitle, fadeInTime, showTime, fadeOutTime);
		} catch (final Exception e1) {
			try {
				sendTitlePacketOld(player, title, subtitle, fadeInTime, showTime, fadeOutTime);
			} catch (final Exception e2) {
				KothPlugin.getInstance().getLogger()
						.info("Failed to send title packet to player " + player.getName() + "!");
			}
		}
	}
}
