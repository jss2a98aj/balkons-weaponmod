package ckathode.weaponmod.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.PhysHelper;
import ckathode.weaponmod.WeaponDamageSource;

public class EntityDynamite extends EntityProjectile {

    private int explodefuse;
    private boolean extinguished;

    public EntityDynamite(World world) {
        super(world);
        setPickupMode(NO_PICKUP);
        extinguished = false;
        explodefuse = rand.nextInt(30) + 20;
    }

    public EntityDynamite(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1, d2);
    }

    public EntityDynamite(World world, EntityLivingBase entityliving, int i) {
        this(world);
        shootingEntity = entityliving;
        setLocationAndAngles(
                entityliving.posX,
                entityliving.posY + entityliving.getEyeHeight(),
                entityliving.posZ,
                entityliving.rotationYaw,
                entityliving.rotationPitch);
        posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
        posY -= 0.1D;
        posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
        setPosition(posX, posY, posZ);
        motionX = -MathHelper.sin((rotationYaw / 180F) * 3.141593F)
                * MathHelper.cos((rotationPitch / 180F) * 3.141593F);
        motionZ = MathHelper.cos((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F);
        motionY = -MathHelper.sin((rotationPitch / 180F) * 3.141593F);
        setThrowableHeading(motionX, motionY, motionZ, 0.7F, 4.0F);
        explodefuse = i;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!inGround && !beenInGround) {
            rotationPitch -= 50F;
        } else {
            rotationPitch = 180F;
        }

        if (isInWater()) if (!extinguished) {
            extinguished = true;
            worldObj.playSoundAtEntity(this, "random.fizz", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
            for (int k = 0; k < 8; k++) {
                float f6 = 0.25F;
                worldObj.spawnParticle(
                        "explode",
                        posX - motionX * f6,
                        posY - motionY * f6,
                        posZ - motionZ * f6,
                        motionX,
                        motionY,
                        motionZ);
            }
        }

        explodefuse--;
        if (!extinguished) if (explodefuse <= 0) {
            detonate();
            setDead();
        } else if (explodefuse > 0) {
            worldObj.spawnParticle("smoke", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void onEntityHit(Entity entity) {
        DamageSource damagesource = null;
        if (shootingEntity == null) {
            damagesource = WeaponDamageSource.causeProjectileWeaponDamage(this, this);
        } else {
            damagesource = WeaponDamageSource.causeProjectileWeaponDamage(this, shootingEntity);
        }
        if (entity.attackEntityFrom(damagesource, 1)) {
            applyEntityHitEffects(entity);
            playHitSound();
            setVelocity(0D, 0D, 0D);
            ticksInAir = 0;
        }
    }

    @Override
    public void onGroundHit(MovingObjectPosition mop) {
        xTile = mop.blockX;
        yTile = mop.blockY;
        zTile = mop.blockZ;
        inTile = worldObj.getBlock(xTile, yTile, zTile);
        motionX = (float) (mop.hitVec.xCoord - posX);
        motionY = (float) (mop.hitVec.yCoord - posY);
        motionZ = (float) (mop.hitVec.zCoord - posZ);
        float f1 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        posX -= (motionX / f1) * 0.05D;
        posY -= (motionY / f1) * 0.05D;
        posZ -= (motionZ / f1) * 0.05D;

        motionX *= -0.2D;
        motionZ *= -0.2D;
        if (mop.sideHit == 1) {
            inGround = true;
            beenInGround = true;
        } else {
            inGround = false;
            worldObj.playSoundAtEntity(this, "random.fizz", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
        }

        if (inTile != null) {
            inTile.onEntityCollidedWithBlock(worldObj, xTile, yTile, zTile, this);
        }
    }

    private void detonate() {
        if (worldObj.isRemote) return;
        if (extinguished) if (ticksInGround >= 200 || ticksInAir >= 200) {
            setDead();
        }
        float f = 2.0F;
        PhysHelper.createAdvancedExplosion(
                worldObj,
                this,
                posX,
                posY,
                posZ,
                f,
                BalkonsWeaponMod.instance.modConfig.dynamiteDoesBlockDamage,
                true);
    }

    @Override
    public boolean aimRotation() {
        return false;
    }

    @Override
    public int getMaxArrowShake() {
        return 0;
    }

    @Override
    public ItemStack getPickupItem() {
        return new ItemStack(BalkonsWeaponMod.dynamite, 1);
    }

    @Override
    public float getShadowSize() {
        return 0.2F;
    }

    @Override
    public void playHitSound() {
        worldObj.playSoundAtEntity(this, "random.fizz", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setByte("fuse", (byte) explodefuse);
        nbttagcompound.setBoolean("off", extinguished);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        explodefuse = nbttagcompound.getByte("fuse");
        extinguished = nbttagcompound.getBoolean("off");
    }
}
