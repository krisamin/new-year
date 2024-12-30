package kr.isamin.newYear.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordCommandManager extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping" -> {
                event.reply("퐁!").queue();
            }
            case "verify" -> {
                String code = event.getOption("code").getAsString();

                DiscordManager discordManager = DiscordManager.getInstance();
                UserManager manager = UserManager.getInstance();

                Guild guild = discordManager.guild;

                Member member = event.getMember();
                if (member == null) {
                    event.reply("서버에 먼저 가입해주세요.").queue();
                    return;
                }

                String nickname = member.getNickname();
                if (nickname == null) {
                    event.reply("닉네임이 설정되지 않았습니다.").queue();
                    return;
                }

                boolean result = manager.verify(code, member.getId(), nickname);
                if (result) {
                    Role role = guild.getRoleById(discordManager.getRole());
                    if (role != null) {
                        guild.addRoleToMember(member, role).queue();
                    }
                }

                event.reply(result ? "인증을 성공하였습니다." : "인증을 실패하였습니다.").queue();
            }
            default -> {
                return;
            }
        }
    }
}
