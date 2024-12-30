package kr.isamin.newYear.events;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.DiscordManager;
import kr.isamin.newYear.objects.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventListener implements Listener {
    private final NewYear plugin;

    public PlayerDeathEventListener(NewYear plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UserManager manager = UserManager.getInstance();

        Component originalDeathMessage = event.deathMessage();

        DiscordManager discordManager = DiscordManager.getInstance();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            discordManager.send(
                    MiniMessage.miniMessage().serialize(manager.getNickname(player)),
                    "# 죽음",
                    player.getUniqueId()
            );
        });

        Component deathMessage = Component.text()
                .append(manager.getNickname(player).color(NamedTextColor.YELLOW))
                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                .append(Component.text(getDeathCause(player.getLastDamageCause())).color(NamedTextColor.WHITE))
                .append(Component.text(" "))
                .append(originalDeathMessage.color(NamedTextColor.GRAY))
                .build();

        event.deathMessage(deathMessage);
    }

    private String getDeathCause(EntityDamageEvent damageEvent) {
        if (damageEvent == null) return "알 수 없는 이유로 사망";

        return switch (damageEvent.getCause()) {
            case KILL -> "살해당함";
            case WORLD_BORDER -> "세계 경계 밖으로 나가 사망";
            case CONTACT -> "가시에 찔려 사망";
            case ENTITY_ATTACK -> "전투 중 사망";
            case ENTITY_SWEEP_ATTACK -> "휘두르기 공격으로 사망";
            case PROJECTILE -> "발사체에 맞아 사망";
            case SUFFOCATION -> "질식사";
            case FALL -> "높은 곳에서 떨어져 사망";
            case FIRE -> "불에 타 사망";
            case FIRE_TICK -> "불에 타 사망";
            case MELTING -> "녹아내림";
            case LAVA -> "용암에 빠져 사망";
            case DROWNING -> "물에 빠져 익사";
            case BLOCK_EXPLOSION -> "폭발로 인해 사망";
            case ENTITY_EXPLOSION -> "폭발로 인해 사망";
            case VOID -> "끝없는 나락으로 떨어져 사망";
            case LIGHTNING -> "번개에 맞아 사망";
            case SUICIDE -> "자살";
            case STARVATION -> "굶어 죽음";
            case POISON -> "독에 중독되어 사망";
            case MAGIC -> "마법으로 인해 사망";
            case WITHER -> "시듦 효과로 사망";
            case FALLING_BLOCK -> "떨어지는 블록에 깔려 사망";
            case THORNS -> "가시 갑옷에 찔려 사망";
            case DRAGON_BREATH -> "드래곤의 숨결을 맞아 사망";
            case FLY_INTO_WALL -> "벽에 부딪혀 사망";
            case HOT_FLOOR -> "마그마에 발이 데어 사망";
            case CAMPFIRE -> "모닥불에 타 사망";
            case CRAMMING -> "너무 많은 개체에 깔려 사망";
            case DRYOUT -> "탈수로 사망";
            case FREEZE -> "얼어 죽음";
            case SONIC_BOOM -> "음파 공격으로 사망";
            default -> "알 수 없는 이유로 사망";
        };
    }
}
