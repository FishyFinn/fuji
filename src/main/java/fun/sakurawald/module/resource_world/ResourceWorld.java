package fun.sakurawald.module.resource_world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

public class ResourceWorld extends ServerWorld {

    public ResourceWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState) {
        super(server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener, debugWorld, seed, spawners, shouldTickTime, randomSequencesState);
    }

    /*
        The main issue is that the runtime world must return the custom seed through World#getSeed, including within the ServerWorld constructor.
        The solution is to override World#getSeed in a way that the seed is initialized before it is called.
        Please note that: all the resource world will not save its data (properties) into level.dat, so if you restart the server.
        then the seed of resource world will be changed randomly (and then the chunk generator will generate new chunks with the new seed).
     */
    @Override
    public long getSeed() {
        return ((ResourceWorldProperties) this.properties).getSeed();
    }
}
