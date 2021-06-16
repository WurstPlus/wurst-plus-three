package me.travis.wurstplusthree.hack.hacks.misc;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.util.ClientMessage;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.play.server.*;

import java.util.Arrays;

@Hack.Registration(name = "Packet Logger", description = "Logs incoming and outgoing packets", category = Hack.Category.MISC, priority = HackPriority.Lowest)
public final class PacketLogger extends Hack{
    BooleanSetting incoming = new BooleanSetting("Receive", true, this);
    BooleanSetting AdvancementInfo = new BooleanSetting("SPacketAdvancementInfo", false, this, s -> incoming.getValue());
    BooleanSetting SAnimation = new BooleanSetting("SPacketAdvancementInfo", false, this, s -> incoming.getValue());
    BooleanSetting SBlockAction= new BooleanSetting("SPacketBlockAction", false, this, s -> incoming.getValue());
    BooleanSetting SBlockBreakAnim = new BooleanSetting("SPacketBlockBreakAnim", false, this, s -> incoming.getValue());
    BooleanSetting SBlockChange  = new BooleanSetting("SPacketBlockChange", false, this, s -> incoming.getValue());
    BooleanSetting SCamera = new BooleanSetting("SPacketCamera", false, this, s -> incoming.getValue());
    BooleanSetting SChat = new BooleanSetting("SPacketChat", false, this, s -> incoming.getValue());
    BooleanSetting SCooldown  = new BooleanSetting("SPacketCooldown", false, this, s -> incoming.getValue());
    BooleanSetting SChunkData  = new BooleanSetting("SPacketChunkData", false, this, s -> incoming.getValue());
    BooleanSetting SChangeGameState  = new BooleanSetting("SPacketChangeGameState", false, this, s -> incoming.getValue());
    BooleanSetting SCloseWindow = new BooleanSetting("SPacketCloseWindow", false, this, s -> incoming.getValue());
    BooleanSetting SCollectItem = new BooleanSetting("SPacketCollectItem", false, this, s -> incoming.getValue());
    BooleanSetting SCombatEvent = new BooleanSetting("SPacketCombatEvent", false, this, s -> incoming.getValue());
    BooleanSetting SConfirmTransaction = new BooleanSetting("SPacketConfirmTransaction", false, this, s -> incoming.getValue());
    BooleanSetting SCustomPayload = new BooleanSetting("SPacketCustomPayload", false, this, s -> incoming.getValue());
    BooleanSetting SCustomSound = new BooleanSetting("SPacketCustomSound", false, this, s -> incoming.getValue());
    BooleanSetting SDestroyEntities= new BooleanSetting("SPacketDestroyEntities", false, this, s -> incoming.getValue());
    BooleanSetting SDisconnect = new BooleanSetting("SPacketDisconnect", false, this, s -> incoming.getValue());
    BooleanSetting SDisplayObjective = new BooleanSetting("SDisplayObjective", false, this, s -> incoming.getValue());
    BooleanSetting SEffect= new BooleanSetting("SPacketEffect", false, this, s -> incoming.getValue());
    BooleanSetting SEntity = new BooleanSetting("SPacketEntity", false, this, s -> incoming.getValue());
    BooleanSetting SEntityAttach = new BooleanSetting("SPacketEntityAttach", false, this, s -> incoming.getValue());
    BooleanSetting SEntityEffect = new BooleanSetting("SPacketEntityEffect", false, this, s -> incoming.getValue());
    BooleanSetting SEntityEquipment = new BooleanSetting("SPacketEntityEquipment", false, this, s -> incoming.getValue());
    BooleanSetting SEntityHeadLook = new BooleanSetting("SPacketEntityHeadLook", false, this, s -> incoming.getValue());
    BooleanSetting SEntityMetadata = new BooleanSetting("SPacketEntityMetadata", false, this, s -> incoming.getValue());
    BooleanSetting SEntityProperties = new BooleanSetting("SPacketEntityProperties", false, this, s -> incoming.getValue());
    BooleanSetting SEntityStatus = new BooleanSetting("SPacketEntityStatus", false, this, s -> incoming.getValue());
    BooleanSetting SEntityTeleport = new BooleanSetting("SPacketEntityTeleport", false, this, s -> incoming.getValue());
    BooleanSetting SEntityVelocity = new BooleanSetting("SPacketEntityVelocity", false, this, s -> incoming.getValue());
    BooleanSetting SExplosion = new BooleanSetting("SPacketExplosion", false, this, s -> incoming.getValue());
    BooleanSetting SEnableCompression = new BooleanSetting("SPacketEnableCompression", false, this, s -> incoming.getValue());
    BooleanSetting SEncryptionRequest = new BooleanSetting("SPacketEncryptionRequest", false, this, s -> incoming.getValue());
    BooleanSetting SHeldItemChange = new BooleanSetting("SPacketHeldItemChange", false, this, s -> incoming.getValue());


    @CommitEvent(priority = EventPriority.LOW)
    public final void incomingEvent(PacketEvent.Receive event){
        if(!incoming.getValue())return;

        if(event.getPacket() instanceof SPacketAdvancementInfo && AdvancementInfo.getValue()){
            SPacketAdvancementInfo s = (SPacketAdvancementInfo) event.getPacket();
            ClientMessage.sendMessage("SPacketAdvancementInfo:\n"
                    + " -Is First Sync: " + s.isFirstSync());
        }
        else if(event.getPacket() instanceof SPacketAnimation && SAnimation.getValue()){
            SPacketAnimation s = (SPacketAnimation) event.getPacket();
            ClientMessage.sendMessage("SPacketAnimation:\n"+
                    " - Animation Type: " + s.getAnimationType()
                    + "\n - Entity Id: " + s.getEntityID()
            );
        }
        else if(event.getPacket() instanceof SPacketCamera && SCamera.getValue()){
            SPacketCamera s = (SPacketCamera) event.getPacket();
            ClientMessage.sendMessage("SPacketCamera:\n"+
                    " - Entity name: " + s.getEntity(mc.world).getName()
                    + "\n - Entity Id: " + s.entityId
            );
        }
        else if(event.getPacket() instanceof SPacketChat && SChat.getValue()){
            SPacketChat s = (SPacketChat) event.getPacket();
            ClientMessage.sendMessage("SPacketChat:\n"+
                    " - Chat Type: " + s.type.name()
                    + "\n - Formatted Text: " + s.chatComponent.getFormattedText()
            );
        }
        else if(event.getPacket() instanceof SPacketBlockAction && SBlockAction.getValue()){
            SPacketBlockAction s = (SPacketBlockAction) event.getPacket();
            ClientMessage.sendMessage("SPacketBlockAction:\n"+
                    " - Block Type Name: " + s.getBlockType().getLocalizedName()
                    + "\n - Block Type: " + s.getBlockType()
                    + "\n - Block Pos: " + s.getBlockPosition()
                    + "\n - Data1: " + s.getData1()
                    + "\n - Data2: " + s.getData2()
            );
        }
        else if(event.getPacket() instanceof SPacketBlockBreakAnim && SBlockBreakAnim.getValue()){
            SPacketBlockBreakAnim s = (SPacketBlockBreakAnim) event.getPacket();
            ClientMessage.sendMessage("SPacketBlockBreakAnim:\n"+
                    " - Break Id: " + s.getBreakerId()
                    + "\n - Block Pos: " + s.getPosition()
                    + "\n - Progress: " + s.getProgress()
            );
        }
        else if(event.getPacket() instanceof SPacketBlockChange && SBlockChange.getValue()){
            SPacketBlockChange s = (SPacketBlockChange) event.getPacket();
            ClientMessage.sendMessage("SPacketBlockChange:\n"+
                    " - Block Pos: " + s.getBlockPosition()
                    + "\n - Block Name: " + s.blockState.getBlock().getLocalizedName()
                    + "\n - Block State: " + s.getBlockState()
            );
        }
        else if(event.getPacket() instanceof SPacketCooldown && SCooldown.getValue()){
            SPacketCooldown s = (SPacketCooldown) event.getPacket();
            ClientMessage.sendMessage("SPacketCooldown:\n"+
                    " - Item: " + s.getItem()
                    + "\n - Ticks: " + s.getTicks()
            );
        }
        else if(event.getPacket() instanceof SPacketChunkData && SChunkData.getValue()){
            SPacketChunkData s = (SPacketChunkData) event.getPacket();
            ClientMessage.sendMessage("SPacketChunkData:\n"+
                    " - Chunk Pos: " + s.getChunkX() +" "+ s.getChunkZ()
            );
        }
        else if(event.getPacket() instanceof SPacketChangeGameState && SChangeGameState.getValue()){
            SPacketChangeGameState s = (SPacketChangeGameState) event.getPacket();
            ClientMessage.sendMessage("SPacketChangeGameState:\n"+
                    " - Game State Value: " + s.getValue()
                    + "\n - Game State: " + s.getGameState()
            );
        }
        else if(event.getPacket() instanceof SPacketCloseWindow && SCloseWindow.getValue()){
            ClientMessage.sendMessage("SPacketCloseWindow");
        }
        else if(event.getPacket() instanceof SPacketCollectItem && SCollectItem.getValue()){
            SPacketCollectItem s = (SPacketCollectItem) event.getPacket();
            ClientMessage.sendMessage("SPacketCollectItem:\n"+
                    " - Entity ID: " + s.getEntityID()
                    + "\n - Amount: " + s.getAmount()
                    + "\n - Collected Item Id: " + s.getCollectedItemEntityID()
            );
        }
        else if(event.getPacket() instanceof SPacketCombatEvent && SCombatEvent.getValue()){
            SPacketCombatEvent s = (SPacketCombatEvent) event.getPacket();
            ClientMessage.sendMessage("SPacketCombatEvent:\n"+
                    " - Entity ID: " + s.entityId
                    + "\n - Player Id: " + s.playerId
                    + "\n - Event Name: " + s.eventType.name()
                    + "\n - Duration: " + s.duration
                    + "\n - Death Message: " + s.deathMessage.getFormattedText()
            );
        }
        else if(event.getPacket() instanceof SPacketConfirmTransaction && SConfirmTransaction.getValue()){
            SPacketConfirmTransaction s = (SPacketConfirmTransaction) event.getPacket();
            ClientMessage.sendMessage("SPacketConfirmTransaction:\n"+
                    " - Action Number: " + s.getActionNumber()
                    + "\n - Window Id: " + s.getWindowId()
                    + "\n - Was Accepted: " + s.wasAccepted()
            );
        }
        else if(event.getPacket() instanceof SPacketCustomPayload && SCustomPayload.getValue()){
            SPacketCustomPayload s = (SPacketCustomPayload) event.getPacket();
            ClientMessage.sendMessage("SPacketCustomPayload:\n"+
                    " - Channel Name: " + s.getChannelName()
                    + "\n - Buffer Data: " + s.getBufferData().readString(1000)
            );
        }
        else if(event.getPacket() instanceof SPacketCustomSound && SCustomSound.getValue()){
            SPacketCustomSound s = (SPacketCustomSound) event.getPacket();
            ClientMessage.sendMessage("SPacketCustomSound:\n"+
                    " - Sound Name: " + s.getSoundName()
                    + "\n - Sound Category: " + s.getCategory().getName()
                    + "\n - Sound Pos: " + s.getX() + " " + s.getY() + " " +  s.getZ()
                    + "\n - Sound Pitch: " + s.getPitch()
                    + "\n - Sound Volume: " + s.getVolume()
            );
        }
        else if(event.getPacket() instanceof SPacketDestroyEntities && SDestroyEntities.getValue()){
            SPacketDestroyEntities s = (SPacketDestroyEntities) event.getPacket();
            ClientMessage.sendMessage("SPacketDestroyEntities:\n");
            Arrays.stream(s.getEntityIDs()).forEach(id -> {
                ClientMessage.sendMessage("Removed Id: " + id);
            });
        }
        else if(event.getPacket() instanceof SPacketDisconnect && SDisconnect.getValue()){
            SPacketDisconnect s = (SPacketDisconnect) event.getPacket();
            ClientMessage.sendMessage("SPacketDisconnect:\n"+
                    " - Disconnect Reason: " + s.getReason().getFormattedText()
            );
        }
        else if(event.getPacket() instanceof SPacketDisplayObjective && SDisplayObjective.getValue()){
            SPacketDisplayObjective s = (SPacketDisplayObjective) event.getPacket();
            ClientMessage.sendMessage("SPacketDisplayObjective:\n"+
                    " - Objective Name: " + s.getName()
                    + "\n - Objective Pos: " + s.getPosition()
            );
        }
        else if(event.getPacket() instanceof SPacketEffect && SEffect.getValue()){
            SPacketEffect s = (SPacketEffect) event.getPacket();
            ClientMessage.sendMessage("SPacketEffect:\n"+
                    " - Sound Data: " + s.getSoundData()
                    + "\n - Sound Pos: " + s.getSoundPos()
                    + "\n - Sound Type: " + s.getSoundType()
                    + "\n - Is Sound Server Wide: " + s.isSoundServerwide()
            );
        }
        else if(event.getPacket() instanceof SPacketEntity && SEntity.getValue()){
            SPacketEntity s = (SPacketEntity) event.getPacket();
            ClientMessage.sendMessage("SPacketEntity:\n"+
                    " - Entity Name: " + s.getEntity(mc.world).getName()
                    + "\n - Entity Id: " + s.getEntity(mc.world).entityId
                    + "\n - Entity Pitch: " + s.getPitch()
                    + "\n - Is Entity OnGround: " + s.getOnGround()
                    + "\n - Entity Yaw: " + s.getYaw()
                    + "\n - Entity Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityAttach && SEntityAttach.getValue()){
            SPacketEntityAttach s = (SPacketEntityAttach) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityAttach:\n"+
                    " - Entity Id: " + s.getEntityId()
                    + "\n - Entity Vehicle Id: " + s.getVehicleEntityId()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityEffect && SEntityEffect.getValue()){
            SPacketEntityEffect s = (SPacketEntityEffect) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityEffect:\n"+
                    " - Entity Id: " + s.getEntityId()
                    + "\n - Effect Amplifier: " + s.getAmplifier()
                    + "\n - Effect ID: " + s.getEffectId()
                    + "\n - Effect Duration: " + s.getDuration()
                    + "\n - Is Effect Ambient: " + s.getIsAmbient()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityEquipment && SEntityEquipment.getValue()){
            SPacketEntityEquipment s = (SPacketEntityEquipment) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityEquipment:\n"+
                    " - Entity Id: " + s.getEntityID()
                    + "\n - Equipment Slot Name: " + s.getEquipmentSlot().getName()
                    + "\n - Item Name: " + s.getItemStack().getDisplayName()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityHeadLook && SEntityHeadLook.getValue()){
            SPacketEntityHeadLook s = (SPacketEntityHeadLook) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityHeadLook:\n"+
                    " - Entity Id: " + s.getEntity(mc.world).entityId
                    + "\n - Entity Name: " + s.getEntity(mc.world).getName()
                    + "\n - Yaw: " + s.getYaw()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityMetadata && SEntityMetadata.getValue()){
            SPacketEntityMetadata s = (SPacketEntityMetadata) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityMetadata:\n"+
                    " - Entity Id: " + s.getEntityId()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityProperties && SEntityProperties.getValue()){
            SPacketEntityProperties s = (SPacketEntityProperties) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityProperties:\n"+
                    " - Entity Id: " + s.getEntityId()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityStatus && SEntityStatus.getValue()){
            SPacketEntityStatus s = (SPacketEntityStatus) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityStatus:\n"+
                    " - Entity Id: " + s.getEntity(mc.world).getEntityId()
                    + "\n - Entity Name: " + s.getEntity(mc.world).getName()
                    + "\n - Entity OP code: "+ s.getOpCode()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityTeleport && SEntityTeleport.getValue()){
            SPacketEntityTeleport s = (SPacketEntityTeleport) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityTeleport:\n"+
                    " - Entity Id: " + s.getEntityId()
                    + "\n - Entity Pos: "+ s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - Entity Yaw: " + s.getYaw()
                    + "\n - Entity Pitch: " + s.getPitch()
                    + "\n - Is Entity On Ground: " + s.getOnGround()
            );
        }
        else if(event.getPacket() instanceof SPacketEntityVelocity && SEntityVelocity.getValue()){
            SPacketEntityVelocity s = (SPacketEntityVelocity) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityVelocity:\n"+
                    " - Entity Id: " + s.getEntityID()
                    + "\n - MotionX: "+ s.motionX
                    + "\n - MotionY: " + s.motionY
                    + "\n - MotionZ: " + s.motionZ
            );
        }
        else if(event.getPacket() instanceof SPacketExplosion && SExplosion.getValue()){
            SPacketExplosion s = (SPacketExplosion) event.getPacket();
            ClientMessage.sendMessage("SPacketExplosion:\n"+
                    " - Explosion Pos: " + s.posX + " " + s.getY() + " " + s.getZ()
                    + "\n - MotionX: "+ s.motionX
                    + "\n - MotionY: " + s.motionY
                    + "\n - MotionZ: " + s.motionZ
                    + "\n - Strength: " + s.getStrength()
            );
        }
        else if(event.getPacket() instanceof SPacketEnableCompression && SEnableCompression.getValue()){
            SPacketEnableCompression s = (SPacketEnableCompression) event.getPacket();
            ClientMessage.sendMessage("SPacketEnableCompression:\n"+
                    " - Compression Threshold: " + s.getCompressionThreshold()
            );
        }

        else if(event.getPacket() instanceof SPacketEncryptionRequest && SEncryptionRequest.getValue()){
            SPacketEncryptionRequest s = (SPacketEncryptionRequest) event.getPacket();
            ClientMessage.sendMessage("SPacketEncryptionRequest:\n"+
                    " - Server Id: " + s.getServerId()
                    + "\n - Public key: "+ s.getPublicKey()
            );
        }
        else if(event.getPacket() instanceof SPacketHeldItemChange && SHeldItemChange.getValue()){
            SPacketHeldItemChange s = (SPacketHeldItemChange) event.getPacket();
            ClientMessage.sendMessage("SPacketEncryptionRequest:\n"+
                    " - Held Item Hotbar Index: " + s.getHeldItemHotbarIndex()
            );
        }
    }
}
