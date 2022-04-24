package ne.fnfal113.fnamplifications.gems.implementation;

import lombok.Getter;
import ne.fnfal113.fnamplifications.utils.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@SuppressWarnings("ConstantConditions")
public class GuardianTask extends BukkitRunnable {
    
    @Getter
    private final Zombie zombie;
    @Getter
    private final Player player;

    public GuardianTask(Player player, EntityDamageByEntityEvent event) {
        this.player = player;
        this.zombie = player.getWorld().spawn(getPlayer().getLocation(), Zombie.class);
        this.spawnZombie(event);
    }

    @Override
    public void run() {
        if(getZombie().getTarget() == null) {
            Vector vec = getPlayer().getLocation().getDirection();
            Vector finalVec = vec.clone().setX(-vec.getZ()).setZ(vec.getX()).setY(0.8);
            getZombie().teleport(getPlayer().getLocation().add(finalVec.multiply(1)));
        }

        if(getZombie().isDead() || !getPlayer().isOnline() || getPlayer().getEquipment().getChestplate() == null){
            getZombie().remove();
            this.cancel();
        }
    }

    public void spawnZombie(EntityDamageByEntityEvent event){
        getZombie().setCustomName(getPlayer().getName() + "'s Guardian");
        getZombie().setCustomNameVisible(true);
        getZombie().setGlowing(true);
        getZombie().setAI(true);
        getZombie().setBaby(true);

        getZombie().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4F);
        getZombie().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        getZombie().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(7.0D);

        getZombie().getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
        getZombie().getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        getZombie().getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        getZombie().getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        getZombie().getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        getZombie().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0, false, false, false));
        getZombie().setHealth(40.0D);

        getZombie().getPersistentDataContainer().set(Keys.GUARDIAN_KEY, PersistentDataType.STRING, player.getName());
        getZombie().setPersistent(false);
        getZombie().setRemoveWhenFarAway(true);

        if(event.getDamager().getPersistentDataContainer().has(Keys.GUARDIAN_KEY, PersistentDataType.STRING)){
            getZombie().setTarget(Bukkit.getPlayer(event.getDamager().getPersistentDataContainer().get(Keys.GUARDIAN_KEY, PersistentDataType.STRING)));
        } else {
            if (!(event.getDamager() instanceof LivingEntity)) return;
            if (!(event.getDamager() instanceof Projectile)) return;
            Projectile projectileDamager = (Projectile) event.getDamager();
            getZombie().setTarget((LivingEntity) projectileDamager.getShooter());
        }
    }
}