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
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;

import java.util.Arrays;

@Hack.Registration(name = "Packet Logger", description = "Logs incoming and outgoing packets", category = Hack.Category.MISC, priority = HackPriority.Lowest)
public final class PacketLogger extends Hack {
    BooleanSetting incoming = new BooleanSetting("Receive", true, this);
    BooleanSetting AdvancementInfo = new BooleanSetting("SPacketAdvancementInfo", false, this, s -> incoming.getValue());
    BooleanSetting SAnimation = new BooleanSetting("SPacketAnimation", false, this, s -> incoming.getValue());
    BooleanSetting SBlockAction = new BooleanSetting("SPacketBlockAction", false, this, s -> incoming.getValue());
    BooleanSetting SBlockBreakAnim = new BooleanSetting("SPacketBlockBreakAnim", false, this, s -> incoming.getValue());
    BooleanSetting SBlockChange = new BooleanSetting("SPacketBlockChange", false, this, s -> incoming.getValue());
    BooleanSetting SCamera = new BooleanSetting("SPacketCamera", false, this, s -> incoming.getValue());
    BooleanSetting SChat = new BooleanSetting("SPacketChat", false, this, s -> incoming.getValue());
    BooleanSetting SCooldown = new BooleanSetting("SPacketCooldown", false, this, s -> incoming.getValue());
    BooleanSetting SChunkData = new BooleanSetting("SPacketChunkData", false, this, s -> incoming.getValue());
    BooleanSetting SChangeGameState = new BooleanSetting("SPacketChangeGameState", false, this, s -> incoming.getValue());
    BooleanSetting SCloseWindow = new BooleanSetting("SPacketCloseWindow", false, this, s -> incoming.getValue());
    BooleanSetting SCollectItem = new BooleanSetting("SPacketCollectItem", false, this, s -> incoming.getValue());
    BooleanSetting SCombatEvent = new BooleanSetting("SPacketCombatEvent", false, this, s -> incoming.getValue());
    BooleanSetting SConfirmTransaction = new BooleanSetting("SPacketConfirmTransaction", false, this, s -> incoming.getValue());
    BooleanSetting SCustomPayload = new BooleanSetting("SPacketCustomPayload", false, this, s -> incoming.getValue());
    BooleanSetting SCustomSound = new BooleanSetting("SPacketCustomSound", false, this, s -> incoming.getValue());
    BooleanSetting SDestroyEntities = new BooleanSetting("SPacketDestroyEntities", false, this, s -> incoming.getValue());
    BooleanSetting SDisconnect = new BooleanSetting("SPacketDisconnect", false, this, s -> incoming.getValue());
    BooleanSetting SDisplayObjective = new BooleanSetting("SPacketDisplayObjective", false, this, s -> incoming.getValue());
    BooleanSetting SEffect = new BooleanSetting("SPacketEffect", false, this, s -> incoming.getValue());
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
    BooleanSetting SJoinGame = new BooleanSetting("SPacketJoinGame", false, this, s -> incoming.getValue());
    BooleanSetting SKeepAlive = new BooleanSetting("SPacketKeepAlive", false, this, s -> incoming.getValue());
    BooleanSetting SLoginSuccess = new BooleanSetting("SPacketLoginSuccess", false, this, s -> incoming.getValue());
    BooleanSetting SMaps = new BooleanSetting("SPacketMaps", false, this, s -> incoming.getValue());
    BooleanSetting SMoveVehicle = new BooleanSetting("SPacketMoveVehicle", false, this, s -> incoming.getValue());
    BooleanSetting SMultiBlockChange = new BooleanSetting("SPacketMultiBlockChange", false, this, s -> incoming.getValue());
    BooleanSetting SOpenWindow = new BooleanSetting("SPacketOpenWindow", false, this, s -> incoming.getValue());
    BooleanSetting SParticles = new BooleanSetting("SPacketParticles", false, this, s -> incoming.getValue());
    BooleanSetting SPlayerAbilities = new BooleanSetting("SPacketPlayerAbilities", false, this, s -> incoming.getValue());
    BooleanSetting SPlayerListHeaderFooter = new BooleanSetting("SPacketPlayerListHeaderFooter", false, this, s -> incoming.getValue());
    BooleanSetting SPlayerListItem = new BooleanSetting("SPacketPlayerListItem", false, this, s -> incoming.getValue());
    BooleanSetting SPlayerPosLook = new BooleanSetting("SPacketPlayerPosLook", false, this, s -> incoming.getValue());
    BooleanSetting SPong = new BooleanSetting("SPacketPong", false, this, s -> incoming.getValue());
    BooleanSetting SRecipeBook = new BooleanSetting("SPacketRecipeBook", false, this, s -> incoming.getValue());
    BooleanSetting SRespawn = new BooleanSetting("SPacketRespawn", false, this, s -> incoming.getValue());
    BooleanSetting SRemoveEntityEffect = new BooleanSetting("SPacketRemoveEntityEffect", false, this, s -> incoming.getValue());
    BooleanSetting SScoreboardObjective = new BooleanSetting("SPacketScoreboardObjective", false, this, s -> incoming.getValue());
    BooleanSetting SServerDifficulty = new BooleanSetting("SPacketServerDifficulty", false, this, s -> incoming.getValue());
    BooleanSetting SSelectAdvancementsTab = new BooleanSetting("SPacketSelectAdvancementsTab", false, this, s -> incoming.getValue());
    BooleanSetting SServerInfo = new BooleanSetting("SPacketServerInfo", false, this, s -> incoming.getValue());
    BooleanSetting SSetExperience = new BooleanSetting("SPacketSetExperience", false, this, s -> incoming.getValue());
    BooleanSetting SSetPassengers = new BooleanSetting("SPacketSetPassengers", false, this, s -> incoming.getValue());
    BooleanSetting SSetSlot = new BooleanSetting("SPacketSetSlot", false, this, s -> incoming.getValue());
    BooleanSetting SSignEditorOpen = new BooleanSetting("SPacketSignEditorOpen", false, this, s -> incoming.getValue());
    BooleanSetting SSoundEffect = new BooleanSetting("SPacketSoundEffect", false, this, s -> incoming.getValue());
    BooleanSetting SSpawnGlobalEntity = new BooleanSetting("SPacketSpawnGlobalEntity", false, this, s -> incoming.getValue());
    BooleanSetting SSpawnMob = new BooleanSetting("SPacketSpawnMob", false, this, s -> incoming.getValue());
    BooleanSetting SSpawnPlayer = new BooleanSetting("SPacketSpawnPlayer", false, this, s -> incoming.getValue());
    BooleanSetting SSpawnExperienceOrb = new BooleanSetting("SPacketSpawnExperienceOrb", false, this, s -> incoming.getValue());
    BooleanSetting SSpawnPainting = new BooleanSetting("SPacketSpawnPainting", false, this, s -> incoming.getValue());
    BooleanSetting SSpawnObject = new BooleanSetting("SPacketSpawnObject", false, this, s -> incoming.getValue());
    BooleanSetting SSpawnPosition = new BooleanSetting("SPacketSpawnPosition", false, this, s -> incoming.getValue());
    BooleanSetting STabComplete = new BooleanSetting("SPacketTabComplete", false, this, s -> incoming.getValue());
    BooleanSetting SUnloadChunk= new BooleanSetting("SPacketUnloadChunk", false, this, s -> incoming.getValue());
    BooleanSetting SUseBed =  new BooleanSetting("SPacketUseBed", false, this, s -> incoming.getValue());
    BooleanSetting SUpdateHealth=  new BooleanSetting("SPacketUpdateHealth", false, this, s -> incoming.getValue());


    BooleanSetting outgoing = new BooleanSetting("Outgoing", true, this);
    BooleanSetting CAnimation =  new BooleanSetting("CPacketAnimation", false, this, s -> outgoing.getValue());
    BooleanSetting CChatMessage =  new BooleanSetting("CPacketChatMessage", false, this, s -> outgoing.getValue());
    BooleanSetting CClickWindow =  new BooleanSetting("CPacketClickWindow", false, this, s -> outgoing.getValue());
    BooleanSetting CConfirmTeleport =  new BooleanSetting("CPacketConfirmTeleport", false, this, s -> outgoing.getValue());
    BooleanSetting CClientStatus =  new BooleanSetting("CPacketClientStatus", false, this, s -> outgoing.getValue());
    BooleanSetting CCustomPayload =  new BooleanSetting("CPacketCustomPayload", false, this, s -> outgoing.getValue());
    BooleanSetting CCreativeInventoryAction = new BooleanSetting("CPacketCreativeInventoryAction", false, this, s -> outgoing.getValue());



    @CommitEvent(priority = EventPriority.LOW)
    public final void outgoingEvent(PacketEvent.Send event){
        if(!outgoing.getValue())return;

        if(event.getPacket() instanceof CPacketAnimation && CAnimation.getValue()){
            CPacketAnimation s = (CPacketAnimation) event.getPacket();
            ClientMessage.sendMessage("CPacketAnimation"
                    +"\n - Hand name: " + s.getHand().name()
            );
        } else if(event.getPacket() instanceof CPacketChatMessage && CChatMessage.getValue()){
            CPacketChatMessage s = (CPacketChatMessage) event.getPacket();
            ClientMessage.sendMessage("CPacketChatMessage"
                    +"\n - Message: " + s.message
            );
        } else if(event.getPacket() instanceof CPacketClickWindow && CClickWindow.getValue()){
            CPacketClickWindow s = (CPacketClickWindow) event.getPacket();
            ClientMessage.sendMessage("CPacketClickWindow"
                    +"\n - Acton Number: " + s.getActionNumber()
                    +"\n - Window ID: " + s.getWindowId()
                    +"\n - Item Name: " + s.getClickedItem().getDisplayName()
                    +"\n - Click Type Name: " + s.getClickType().name()
            );
        } else if(event.getPacket() instanceof CPacketConfirmTeleport && CConfirmTeleport.getValue()){
            CPacketConfirmTeleport s = (CPacketConfirmTeleport) event.getPacket();
            ClientMessage.sendMessage("CPacketConfirmTeleport"
                    +"\n - Tp id: " + s.getTeleportId()
            );
        } else if(event.getPacket() instanceof CPacketClientStatus && CClientStatus.getValue()){
            CPacketClientStatus s = (CPacketClientStatus) event.getPacket();
            ClientMessage.sendMessage("CPacketClientStatus"
                    +"\n - Status Name: " + s.getStatus().name()
            );
        } else if(event.getPacket() instanceof CPacketCustomPayload && CCustomPayload.getValue()){
            CPacketCustomPayload s = (CPacketCustomPayload) event.getPacket();
            ClientMessage.sendMessage("CPacketCustomPayload"
                    +"\n - Channel: " + s.channel
                    +"\n - Data: " + s.data.readString(10000)
            );
        } else if(event.getPacket() instanceof CPacketCreativeInventoryAction && CCreativeInventoryAction.getValue()){
            CPacketCreativeInventoryAction s = (CPacketCreativeInventoryAction) event.getPacket();
            ClientMessage.sendMessage("CPacketCreativeInventoryAction"
                    +"\n - Item name: " + s.getStack().getDisplayName()
                    +"\n - Slot Id: " + s.getSlotId()
            );
        }

    }


    @CommitEvent(priority = EventPriority.LOW)
    public final void incomingEvent(PacketEvent.Receive event) {
        if (!incoming.getValue()) return;

        if (event.getPacket() instanceof SPacketAdvancementInfo && AdvancementInfo.getValue()) {
            SPacketAdvancementInfo s = (SPacketAdvancementInfo) event.getPacket();
            ClientMessage.sendMessage("SPacketAdvancementInfo:\n"
                    + " -Is First Sync: " + s.isFirstSync());
        } else if (event.getPacket() instanceof SPacketAnimation && SAnimation.getValue()) {
            SPacketAnimation s = (SPacketAnimation) event.getPacket();
            ClientMessage.sendMessage("SPacketAnimation:\n" +
                    " - Animation Type: " + s.getAnimationType()
                    + "\n - Entity Id: " + s.getEntityID()
            );
        } else if (event.getPacket() instanceof SPacketCamera && SCamera.getValue()) {
            SPacketCamera s = (SPacketCamera) event.getPacket();
            ClientMessage.sendMessage("SPacketCamera:\n" +
                    " - Entity name: " + s.getEntity(mc.world).getName()
                    + "\n - Entity Id: " + s.entityId
            );
        } else if (event.getPacket() instanceof SPacketChat && SChat.getValue()) {
            SPacketChat s = (SPacketChat) event.getPacket();
            ClientMessage.sendMessage("SPacketChat:\n" +
                    " - Chat Type: " + s.type.name()
                    + "\n - Formatted Text: " + s.chatComponent.getFormattedText()
            );
        } else if (event.getPacket() instanceof SPacketBlockAction && SBlockAction.getValue()) {
            SPacketBlockAction s = (SPacketBlockAction) event.getPacket();
            ClientMessage.sendMessage("SPacketBlockAction:\n" +
                    " - Block Type Name: " + s.getBlockType().getLocalizedName()
                    + "\n - Block Type: " + s.getBlockType()
                    + "\n - Block Pos: " + s.getBlockPosition()
                    + "\n - Data1: " + s.getData1()
                    + "\n - Data2: " + s.getData2()
            );
        } else if (event.getPacket() instanceof SPacketBlockBreakAnim && SBlockBreakAnim.getValue()) {
            SPacketBlockBreakAnim s = (SPacketBlockBreakAnim) event.getPacket();
            ClientMessage.sendMessage("SPacketBlockBreakAnim:\n" +
                    " - Break Id: " + s.getBreakerId()
                    + "\n - Block Pos: " + s.getPosition()
                    + "\n - Progress: " + s.getProgress()
            );
        } else if (event.getPacket() instanceof SPacketBlockChange && SBlockChange.getValue()) {
            SPacketBlockChange s = (SPacketBlockChange) event.getPacket();
            ClientMessage.sendMessage("SPacketBlockChange:\n" +
                    " - Block Pos: " + s.getBlockPosition()
                    + "\n - Block Name: " + s.blockState.getBlock().getLocalizedName()
                    + "\n - Block State: " + s.getBlockState()
            );
        } else if (event.getPacket() instanceof SPacketCooldown && SCooldown.getValue()) {
            SPacketCooldown s = (SPacketCooldown) event.getPacket();
            ClientMessage.sendMessage("SPacketCooldown:\n" +
                    " - Item: " + s.getItem()
                    + "\n - Ticks: " + s.getTicks()
            );
        } else if (event.getPacket() instanceof SPacketChunkData && SChunkData.getValue()) {
            SPacketChunkData s = (SPacketChunkData) event.getPacket();
            ClientMessage.sendMessage("SPacketChunkData:\n" +
                    " - Chunk Pos: " + s.getChunkX() + " " + s.getChunkZ()
            );
        } else if (event.getPacket() instanceof SPacketChangeGameState && SChangeGameState.getValue()) {
            SPacketChangeGameState s = (SPacketChangeGameState) event.getPacket();
            ClientMessage.sendMessage("SPacketChangeGameState:\n" +
                    " - Game State Value: " + s.getValue()
                    + "\n - Game State: " + s.getGameState()
            );
        } else if (event.getPacket() instanceof SPacketCloseWindow && SCloseWindow.getValue()) {
            ClientMessage.sendMessage("SPacketCloseWindow" );
        } else if (event.getPacket() instanceof SPacketCollectItem && SCollectItem.getValue()) {
            SPacketCollectItem s = (SPacketCollectItem) event.getPacket();
            ClientMessage.sendMessage("SPacketCollectItem:\n" +
                    " - Entity ID: " + s.getEntityID()
                    + "\n - Amount: " + s.getAmount()
                    + "\n - Collected Item Id: " + s.getCollectedItemEntityID()
            );
        } else if (event.getPacket() instanceof SPacketCombatEvent && SCombatEvent.getValue()) {
            SPacketCombatEvent s = (SPacketCombatEvent) event.getPacket();
            ClientMessage.sendMessage("SPacketCombatEvent:\n" +
                    " - Entity ID: " + s.entityId
                    + "\n - Player Id: " + s.playerId
                    + "\n - Event Name: " + s.eventType.name()
                    + "\n - Duration: " + s.duration
                    + "\n - Death Message: " + s.deathMessage.getFormattedText()
            );
        } else if (event.getPacket() instanceof SPacketConfirmTransaction && SConfirmTransaction.getValue()) {
            SPacketConfirmTransaction s = (SPacketConfirmTransaction) event.getPacket();
            ClientMessage.sendMessage("SPacketConfirmTransaction:\n" +
                    " - Action Number: " + s.getActionNumber()
                    + "\n - Window Id: " + s.getWindowId()
                    + "\n - Was Accepted: " + s.wasAccepted()
            );
        } else if (event.getPacket() instanceof SPacketCustomPayload && SCustomPayload.getValue()) {
            SPacketCustomPayload s = (SPacketCustomPayload) event.getPacket();
            ClientMessage.sendMessage("SPacketCustomPayload:\n" +
                    " - Channel Name: " + s.getChannelName()
                    + "\n - Buffer Data: " + s.getBufferData().readString(1000)
            );
        } else if (event.getPacket() instanceof SPacketCustomSound && SCustomSound.getValue()) {
            SPacketCustomSound s = (SPacketCustomSound) event.getPacket();
            ClientMessage.sendMessage("SPacketCustomSound:\n" +
                    " - Sound Name: " + s.getSoundName()
                    + "\n - Sound Category: " + s.getCategory().getName()
                    + "\n - Sound Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - Sound Pitch: " + s.getPitch()
                    + "\n - Sound Volume: " + s.getVolume()
            );
        } else if (event.getPacket() instanceof SPacketDestroyEntities && SDestroyEntities.getValue()) {
            SPacketDestroyEntities s = (SPacketDestroyEntities) event.getPacket();
            ClientMessage.sendMessage("SPacketDestroyEntities:\n" );
            Arrays.stream(s.getEntityIDs()).forEach(id -> {
                ClientMessage.sendMessage("Removed Id: " + id);
            });
        } else if (event.getPacket() instanceof SPacketDisconnect && SDisconnect.getValue()) {
            SPacketDisconnect s = (SPacketDisconnect) event.getPacket();
            ClientMessage.sendMessage("SPacketDisconnect:\n" +
                    " - Disconnect Reason: " + s.getReason().getFormattedText()
            );
        } else if (event.getPacket() instanceof SPacketDisplayObjective && SDisplayObjective.getValue()) {
            SPacketDisplayObjective s = (SPacketDisplayObjective) event.getPacket();
            ClientMessage.sendMessage("SPacketDisplayObjective:\n" +
                    " - Objective Name: " + s.getName()
                    + "\n - Objective Pos: " + s.getPosition()
            );
        } else if (event.getPacket() instanceof SPacketEffect && SEffect.getValue()) {
            SPacketEffect s = (SPacketEffect) event.getPacket();
            ClientMessage.sendMessage("SPacketEffect:\n" +
                    " - Sound Data: " + s.getSoundData()
                    + "\n - Sound Pos: " + s.getSoundPos()
                    + "\n - Sound Type: " + s.getSoundType()
                    + "\n - Is Sound Server Wide: " + s.isSoundServerwide()
            );
        } else if (event.getPacket() instanceof SPacketEntity && SEntity.getValue()) {
            SPacketEntity s = (SPacketEntity) event.getPacket();
            ClientMessage.sendMessage("SPacketEntity:\n" +
                    " - Entity Name: " + s.getEntity(mc.world).getName()
                    + "\n - Entity Id: " + s.getEntity(mc.world).entityId
                    + "\n - Entity Pitch: " + s.getPitch()
                    + "\n - Is Entity OnGround: " + s.getOnGround()
                    + "\n - Entity Yaw: " + s.getYaw()
                    + "\n - Entity Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
            );
        } else if (event.getPacket() instanceof SPacketEntityAttach && SEntityAttach.getValue()) {
            SPacketEntityAttach s = (SPacketEntityAttach) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityAttach:\n" +
                    " - Entity Id: " + s.getEntityId()
                    + "\n - Entity Vehicle Id: " + s.getVehicleEntityId()
            );
        } else if (event.getPacket() instanceof SPacketEntityEffect && SEntityEffect.getValue()) {
            SPacketEntityEffect s = (SPacketEntityEffect) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityEffect:\n" +
                    " - Entity Id: " + s.getEntityId()
                    + "\n - Effect Amplifier: " + s.getAmplifier()
                    + "\n - Effect ID: " + s.getEffectId()
                    + "\n - Effect Duration: " + s.getDuration()
                    + "\n - Is Effect Ambient: " + s.getIsAmbient()
            );
        } else if (event.getPacket() instanceof SPacketEntityEquipment && SEntityEquipment.getValue()) {
            SPacketEntityEquipment s = (SPacketEntityEquipment) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityEquipment:\n" +
                    " - Entity Id: " + s.getEntityID()
                    + "\n - Equipment Slot Name: " + s.getEquipmentSlot().getName()
                    + "\n - Item Name: " + s.getItemStack().getDisplayName()
            );
        } else if (event.getPacket() instanceof SPacketEntityHeadLook && SEntityHeadLook.getValue()) {
            SPacketEntityHeadLook s = (SPacketEntityHeadLook) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityHeadLook:\n" +
                    " - Entity Id: " + s.getEntity(mc.world).entityId
                    + "\n - Entity Name: " + s.getEntity(mc.world).getName()
                    + "\n - Yaw: " + s.getYaw()
            );
        } else if (event.getPacket() instanceof SPacketEntityMetadata && SEntityMetadata.getValue()) {
            SPacketEntityMetadata s = (SPacketEntityMetadata) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityMetadata:\n" +
                    " - Entity Id: " + s.getEntityId()
            );
        } else if (event.getPacket() instanceof SPacketEntityProperties && SEntityProperties.getValue()) {
            SPacketEntityProperties s = (SPacketEntityProperties) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityProperties:\n" +
                    " - Entity Id: " + s.getEntityId()
            );
        } else if (event.getPacket() instanceof SPacketEntityStatus && SEntityStatus.getValue()) {
            SPacketEntityStatus s = (SPacketEntityStatus) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityStatus:\n" +
                    " - Entity Id: " + s.getEntity(mc.world).getEntityId()
                    + "\n - Entity Name: " + s.getEntity(mc.world).getName()
                    + "\n - Entity OP code: " + s.getOpCode()
            );
        } else if (event.getPacket() instanceof SPacketEntityTeleport && SEntityTeleport.getValue()) {
            SPacketEntityTeleport s = (SPacketEntityTeleport) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityTeleport:\n" +
                    " - Entity Id: " + s.getEntityId()
                    + "\n - Entity Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - Entity Yaw: " + s.getYaw()
                    + "\n - Entity Pitch: " + s.getPitch()
                    + "\n - Is Entity On Ground: " + s.getOnGround()
            );
        } else if (event.getPacket() instanceof SPacketEntityVelocity && SEntityVelocity.getValue()) {
            SPacketEntityVelocity s = (SPacketEntityVelocity) event.getPacket();
            ClientMessage.sendMessage("SPacketEntityVelocity:\n" +
                    " - Entity Id: " + s.getEntityID()
                    + "\n - MotionX: " + s.motionX
                    + "\n - MotionY: " + s.motionY
                    + "\n - MotionZ: " + s.motionZ
            );
        } else if (event.getPacket() instanceof SPacketExplosion && SExplosion.getValue()) {
            SPacketExplosion s = (SPacketExplosion) event.getPacket();
            ClientMessage.sendMessage("SPacketExplosion:\n" +
                    " - Explosion Pos: " + s.posX + " " + s.getY() + " " + s.getZ()
                    + "\n - MotionX: " + s.motionX
                    + "\n - MotionY: " + s.motionY
                    + "\n - MotionZ: " + s.motionZ
                    + "\n - Strength: " + s.getStrength()
            );
        } else if (event.getPacket() instanceof SPacketEnableCompression && SEnableCompression.getValue()) {
            SPacketEnableCompression s = (SPacketEnableCompression) event.getPacket();
            ClientMessage.sendMessage("SPacketEnableCompression:\n" +
                    " - Compression Threshold: " + s.getCompressionThreshold()
            );
        } else if (event.getPacket() instanceof SPacketEncryptionRequest && SEncryptionRequest.getValue()) {
            SPacketEncryptionRequest s = (SPacketEncryptionRequest) event.getPacket();
            ClientMessage.sendMessage("SPacketEncryptionRequest:\n" +
                    " - Server Id: " + s.getServerId()
                    + "\n - Public key: " + s.getPublicKey()
            );
        } else if (event.getPacket() instanceof SPacketHeldItemChange && SHeldItemChange.getValue()) {
            SPacketHeldItemChange s = (SPacketHeldItemChange) event.getPacket();
            ClientMessage.sendMessage("SPacketEncryptionRequest:\n" +
                    " - Held Item Hotbar Index: " + s.getHeldItemHotbarIndex()
            );
        } else if (event.getPacket() instanceof SPacketJoinGame && SJoinGame.getValue()) {
            SPacketJoinGame s = (SPacketJoinGame) event.getPacket();
            ClientMessage.sendMessage("SPacketJoinGame:\n" +
                    " - Player ID: " + s.getPlayerId()
                    + "\n - Difficulty: " + s.getDifficulty().name()
                    + "\n - Dimension: " + s.getDimension()
                    + "\n - Game Type: " + s.getGameType().getName()
                    + "\n - World Type: " + s.getWorldType().getName()
                    + "\n - Max Players: " + s.getMaxPlayers()
                    + "\n - Is Hardcore Mode: " + s.isHardcoreMode()
            );
        } else if (event.getPacket() instanceof SPacketKeepAlive && SKeepAlive.getValue()) {
            SPacketKeepAlive s = (SPacketKeepAlive) event.getPacket();
            ClientMessage.sendMessage("SPacketKeepAlive:\n" +
                    " - ID: " + s.getId()
            );
        } else if (event.getPacket() instanceof SPacketLoginSuccess && SLoginSuccess.getValue()) {
            SPacketLoginSuccess s = (SPacketLoginSuccess) event.getPacket();
            ClientMessage.sendMessage("SPacketLoginSuccess:\n" +
                    " - Name: " + s.getProfile().getName()
            );
        } else if (event.getPacket() instanceof SPacketMaps && SMaps.getValue()) {
            SPacketMaps s = (SPacketMaps) event.getPacket();
            ClientMessage.sendMessage("SPacketMaps:\n" +
                    " - Map ID: " + s.getMapId()
            );
        } else if (event.getPacket() instanceof SPacketMoveVehicle && SMoveVehicle.getValue()) {
            SPacketMoveVehicle s = (SPacketMoveVehicle) event.getPacket();
            ClientMessage.sendMessage("SPacketMoveVehicle:\n" +
                    " - Pitch: " + s.getPitch()
                    + "\n - Yaw: " + s.getYaw()
                    + "\n - Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
            );
        } else if (event.getPacket() instanceof SPacketMultiBlockChange && SMultiBlockChange.getValue()) {
            SPacketMultiBlockChange s = (SPacketMultiBlockChange) event.getPacket();
            ClientMessage.sendMessage("SPacketMultiBlockChange"
            );
        } else if (event.getPacket() instanceof SPacketOpenWindow && SOpenWindow.getValue()) {
            SPacketOpenWindow s = (SPacketOpenWindow) event.getPacket();
            ClientMessage.sendMessage("SPacketOpenWindow:"
                    + "\n - Gui ID: " + s.getGuiId()
                    + "\n - Entity ID: " + s.getEntityId()
                    + "\n - Window ID: " + s.getWindowId()
                    + "\n - Window Title: " + s.getWindowTitle()
                    + "\n - Slot Count: " + s.getSlotCount()
            );
        } else if (event.getPacket() instanceof SPacketParticles && SParticles.getValue()) {
            SPacketParticles s = (SPacketParticles) event.getPacket();
            ClientMessage.sendMessage("SPacketParticles:"
                    + "\n - Particle Count: " + s.getParticleCount()
                    + "\n - Particle Speed: " + s.getParticleSpeed()
                    + "\n - Particle Name: " + s.getParticleType().getParticleName()
                    + "\n - Pos: " + s.getXCoordinate() + " " + s.getYCoordinate() + " " + s.getZCoordinate()
            );
        } else if (event.getPacket() instanceof SPacketPlayerAbilities && SPlayerAbilities.getValue()) {
            SPacketPlayerAbilities s = (SPacketPlayerAbilities) event.getPacket();
            ClientMessage.sendMessage("SPacketPlayerAbilities:"
                    + "\n - Walk Speed: " + s.getWalkSpeed()
                    + "\n - Fly Speed: " + s.getFlySpeed()
                    + "\n - Is Allow Flying: " + s.isAllowFlying()
                    + "\n - Is Creative Mode: " + s.isCreativeMode()
                    + "\n - Is Flying: " + s.isFlying()
                    + "\n - Is Flying: " + s.isInvulnerable()
            );
        } else if (event.getPacket() instanceof SPacketPlayerListHeaderFooter && SPlayerListHeaderFooter.getValue()) {
            SPacketPlayerListHeaderFooter s = (SPacketPlayerListHeaderFooter) event.getPacket();
            ClientMessage.sendMessage("SPacketPlayerListHeaderFooter:"
                    + "\n - Footer: " + s.getFooter().getFormattedText()
                    + "\n - Header: " + s.getHeader()
            );
        } else if (event.getPacket() instanceof SPacketPlayerListItem && SPlayerListItem.getValue()) {
            SPacketPlayerListItem s = (SPacketPlayerListItem) event.getPacket();
            ClientMessage.sendMessage("SPacketPlayerListItem:"
                    + "\n - Action Name: " + s.getAction().name()
            );
        } else if (event.getPacket() instanceof SPacketPlayerPosLook && SPlayerPosLook.getValue()) {
            SPacketPlayerPosLook s = (SPacketPlayerPosLook) event.getPacket();
            ClientMessage.sendMessage("SPacketPlayerPosLook:"
                    + "\n - Pitch: " + s.getPitch()
                    + "\n - Yaw: " + s.getYaw()
                    + "\n - Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - Teleport ID: " + s.getTeleportId()
            );
        } else if (event.getPacket() instanceof SPacketPong && SPong.getValue()) {
            SPacketPong s = (SPacketPong) event.getPacket();
            ClientMessage.sendMessage("SPacketPong" );
        } else if (event.getPacket() instanceof SPacketRecipeBook && SRecipeBook.getValue()) {
            SPacketRecipeBook s = (SPacketRecipeBook) event.getPacket();
            ClientMessage.sendMessage("SPacketRecipeBook" );
        } else if (event.getPacket() instanceof SPacketRespawn && SRespawn.getValue()) {
            SPacketRespawn s = (SPacketRespawn) event.getPacket();
            ClientMessage.sendMessage("SPacketRecipeBook: "
                    + "\n - Dimension ID " + s.getDimensionID()
                    + "\n - WorldType Name " + s.getWorldType().getName()
                    + "\n - Difficulty " + s.getDifficulty().name()
                    + "\n - GameType name " + s.getGameType().name()
            );
        } else if (event.getPacket() instanceof SPacketRemoveEntityEffect && SRemoveEntityEffect.getValue()) {
            SPacketRemoveEntityEffect s = (SPacketRemoveEntityEffect) event.getPacket();
            ClientMessage.sendMessage("SPacketRemoveEntityEffect: "
                    + "\n - Entity Name " + s.getEntity(mc.world).getName()
                    + "\n - Potion Name " + s.getPotion().getName()
                    + "\n - Entity ID " + s.getEntity(mc.world).getEntityId()
            );
        } else if (event.getPacket() instanceof SPacketScoreboardObjective && SScoreboardObjective.getValue()) {
            SPacketScoreboardObjective s = (SPacketScoreboardObjective) event.getPacket();
            ClientMessage.sendMessage("SPacketScoreboardObjective: "
                    + "\n - Objective Name " + s.getObjectiveName()
                    + "\n - Acton " + s.getAction()
                    + "\n - Render Type Name" + s.getRenderType().name()
            );
        } else if (event.getPacket() instanceof SPacketServerDifficulty && SServerDifficulty.getValue()) {
            SPacketServerDifficulty s = (SPacketServerDifficulty) event.getPacket();
            ClientMessage.sendMessage("SPacketServerDifficulty: "
                    + "\n - Difficulty Name " + s.getDifficulty().name()
            );
        } else if (event.getPacket() instanceof SPacketSelectAdvancementsTab && SSelectAdvancementsTab.getValue()) {
            SPacketSelectAdvancementsTab s = (SPacketSelectAdvancementsTab) event.getPacket();
            ClientMessage.sendMessage("SPacketSelectAdvancementsTab" );

        } else if (event.getPacket() instanceof SPacketServerInfo && SServerInfo.getValue()) {
            SPacketServerInfo s = (SPacketServerInfo) event.getPacket();
            ClientMessage.sendMessage("SPacketServerInfo: "
                    + "\n - Server Info " + s.getResponse().getJson()
            );
        } else if (event.getPacket() instanceof SPacketSetExperience && SSetExperience.getValue()) {
            SPacketSetExperience s = (SPacketSetExperience) event.getPacket();
            ClientMessage.sendMessage("SPacketSetExperience: "
                    + "\n - Experience Bar " + s.getExperienceBar()
                    + "\n - Total Experience " + s.getTotalExperience()
                    + "\n - Level " + s.getLevel()
            );
        } else if (event.getPacket() instanceof SPacketSetPassengers && SSetPassengers.getValue()) {
            SPacketSetPassengers s = (SPacketSetPassengers) event.getPacket();
            ClientMessage.sendMessage("SPacketSetPassengers: "
                    + "\n - Entity ID " + s.getEntityId()
                    + "\n - Passengers ID " + s.getPassengerIds()
            );
        } else if (event.getPacket() instanceof SPacketSetSlot && SSetSlot.getValue()) {
            SPacketSetSlot s = (SPacketSetSlot) event.getPacket();
            ClientMessage.sendMessage("SPacketSetSlot: "
                    + "\n - Window ID " + s.getWindowId()
                    + "\n - Slot " + s.getSlot()
                    + "\n - Item Name " + s.getStack().getDisplayName()
            );
        } else if (event.getPacket() instanceof SPacketSignEditorOpen && SSignEditorOpen.getValue()) {
            SPacketSignEditorOpen s = (SPacketSignEditorOpen) event.getPacket();
            ClientMessage.sendMessage("SPacketSignEditorOpen: "
                    + "\n - Sign Pos " + s.getSignPosition()
            );
        } else if (event.getPacket() instanceof SPacketSoundEffect && SSoundEffect.getValue()) {
            SPacketSoundEffect s = (SPacketSoundEffect) event.getPacket();
            ClientMessage.sendMessage("SPacketSoundEffect: "
                    + "\n - Sound Name: " + s.getSound().getSoundName()
                    + "\n - Sound Category: " + s.getCategory().getName()
                    + "\n - Sound Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - Sound Pitch: " + s.getPitch()
                    + "\n - Sound Volume: " + s.getVolume()
            );
        } else if (event.getPacket() instanceof SPacketSpawnGlobalEntity && SSpawnGlobalEntity.getValue()) {
            SPacketSpawnGlobalEntity s = (SPacketSpawnGlobalEntity) event.getPacket();
            ClientMessage.sendMessage("SPacketSpawnGlobalEntity: "
                    + "\n - Entity ID: " + s.getEntityId()
                    + "\n - Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - Type: " + s.getType()
            );
        } else if (event.getPacket() instanceof SPacketSpawnMob && SSpawnMob.getValue()) {
            SPacketSpawnMob s = (SPacketSpawnMob) event.getPacket();
            ClientMessage.sendMessage("SPacketSpawnMob: "
                    + "\n - Entity ID: " + s.getEntityID()
                    + "\n - Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - UUID: " + s.getUniqueId()
                    + "\n - Yaw " + s.getYaw()
                    + "\n - Pitch " + s.getPitch()
                    + "\n - Type: " + s.getEntityType()
            );
        } else if (event.getPacket() instanceof SPacketSpawnPlayer && SSpawnPlayer.getValue()) {
            SPacketSpawnPlayer s = (SPacketSpawnPlayer) event.getPacket();
            ClientMessage.sendMessage("SPacketSpawnPlayer: "
                    + "\n - Entity ID: " + s.getEntityID()
                    + "\n - Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - UUID: " + s.getUniqueId()
                    + "\n - Yaw " + s.getYaw()
                    + "\n - Pitch " + s.getPitch()
            );
        } else if (event.getPacket() instanceof SPacketSpawnExperienceOrb && SSpawnExperienceOrb.getValue()) {
            SPacketSpawnExperienceOrb s = (SPacketSpawnExperienceOrb) event.getPacket();
            ClientMessage.sendMessage("SPacketSpawnExperienceOrb: "
                    + "\n - Entity ID: " + s.getEntityID()
                    + "\n - Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - XP value: " + s.getXPValue()
            );
        } else if (event.getPacket() instanceof SPacketSpawnPainting && SSpawnPainting.getValue()) {
            SPacketSpawnPainting s = (SPacketSpawnPainting) event.getPacket();
            ClientMessage.sendMessage("SPacketSpawnPainting: "
                    + "\n - Entity ID: " + s.getEntityID()
                    + "\n - Title: " + s.getTitle()
                    + "\n - Pos: " + s.getPosition()
                    + "\n - UUID: " + s.getUniqueId()
                    + "\n - Facing: " + s.getFacing().getName()
            );
        } else if (event.getPacket() instanceof SPacketSpawnObject && SSpawnObject.getValue()) {
            SPacketSpawnObject s = (SPacketSpawnObject) event.getPacket();
            ClientMessage.sendMessage("SPacketSpawnObject: "
                    + "\n - Entity ID: " + s.getEntityID()
                    + "\n - Pos: " + s.getX() + " " + s.getY() + " " + s.getZ()
                    + "\n - Speed Pos: " + s.getSpeedX() + " " + s.getSpeedY() + " " + s.getSpeedZ()
                    + "\n - UUID: " + s.getUniqueId()
                    + "\n - Data: " + s.getData()
                    + "\n - Type: " + s.getType()
                    + "\n - Pitch: " + s.getPitch()
                    + "\n - Yaw: " + s.getYaw()
            );
        } else if (event.getPacket() instanceof SPacketSpawnPosition && SSpawnPosition.getValue()) {
            SPacketSpawnPosition s = (SPacketSpawnPosition) event.getPacket();
            ClientMessage.sendMessage("SPacketSpawnPosition: "
                    + "\n - Pos: " + s.getSpawnPos()
            );
        } else if (event.getPacket() instanceof SPacketTabComplete && STabComplete.getValue()) {
            SPacketTabComplete s = (SPacketTabComplete) event.getPacket();
            ClientMessage.sendMessage("SPacketTabComplete"
            );
        } else if (event.getPacket() instanceof SPacketUnloadChunk && SUnloadChunk.getValue()) {
            SPacketUnloadChunk s = (SPacketUnloadChunk) event.getPacket();
            ClientMessage.sendMessage("SPacketUnloadChunk"
                    + "\n - Chunk Pos: " + s.getX() + " " + s.getZ()
            );
        } else if (event.getPacket() instanceof SPacketUseBed && SUseBed.getValue()) {
            SPacketUseBed s = (SPacketUseBed) event.getPacket();
            ClientMessage.sendMessage("SPacketUseBed"
                    + "\n - Pos: " + s.getBedPosition()
                    + "\n - Player name: " + s.getPlayer(mc.world).getName()
            );
        } else if (event.getPacket() instanceof SPacketUpdateHealth && SUpdateHealth.getValue()) {
            SPacketUpdateHealth s = (SPacketUpdateHealth) event.getPacket();
            ClientMessage.sendMessage("SPacketUpdateHealth"
                    + "\n - Health: " + s.getHealth()
                    + "\n - Food: " + s.getFoodLevel()
                    + "\n - Saturation: " + s.getSaturationLevel()
            );
        } else if (event.getPacket() instanceof SPacketUpdateTileEntity && SUpdateHealth.getValue()) {
            SPacketUpdateTileEntity s = (SPacketUpdateTileEntity) event.getPacket();
            ClientMessage.sendMessage("SPacketUpdateTileEntity"
                    + "\n - Pos: " + s.getPos()
                    + "\n - Type: " + s.getTileEntityType()
                    + "\n - NBT tag: " + s.getNbtCompound()
            );
        }
    }
}
