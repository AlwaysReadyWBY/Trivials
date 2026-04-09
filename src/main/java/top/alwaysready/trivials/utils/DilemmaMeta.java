package top.alwaysready.trivials.utils;

import org.bukkit.entity.Interaction;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import top.alwaysready.trivials.Trivials;
import top.alwaysready.trivials.module.wax.WaxHolder;
import top.alwaysready.trivials.module.itemframe.DilemmaItemFrame;

import java.util.Optional;
import java.util.function.Supplier;

public class DilemmaMeta<T> extends FixedMetadataValue {

    public static final Type<DilemmaItemFrame> CUSTOM_ITEM_FRAME = new Type<>("trivials:item_frame") {
        @Override
        protected DilemmaItemFrame newValue() {
            return new DilemmaItemFrame();
        }

        @Override
        public Optional<DilemmaItemFrame> get(Metadatable src) {
            Optional<DilemmaItemFrame> opt = super.get(src);
            if (opt.isPresent()) return opt;
            if (src instanceof Interaction interaction
                    && interaction.getScoreboardTags().contains("trivials:item_frame")) {
                DilemmaItemFrame frame = newValue();
                frame.onLoad(interaction);
                src.setMetadata(getKey(), new DilemmaMeta<>(frame));
                return Optional.of(frame);
            }
            return Optional.empty();
        }
    };

    public static final Type<WaxHolder> WAX_INFO = new Type<>("trivials:wax_info") {

        @Override
        protected WaxHolder newValue() {
            return new WaxHolder();
        }
    };

    public static <T> Type<T> type(String key, Supplier<T> supplier){
        return new Type<>(key) {
            @Override
            protected T newValue() {
                return supplier.get();
            }
        };
    }

    public DilemmaMeta(T value) {
        super(Trivials.getInstance(), value);
    }

    public static abstract class Type<T>{
        private final String key;

        public Type(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        protected abstract T newValue();

        @SuppressWarnings("unchecked")
        public T getOrCreate(Metadatable src){
            return get(src).orElseGet(()->{
                        DilemmaMeta<T> meta = new DilemmaMeta<>(newValue());
                        src.setMetadata(getKey(),meta);
                        return (T)meta.value();
                    });
        }

        @SuppressWarnings("unchecked")
        public Optional<T> get(Metadatable src){
            return src.getMetadata(getKey()).stream()
                    .filter(DilemmaMeta.class::isInstance)
                    .map(DilemmaMeta.class::cast)
                    .map(FixedMetadataValue::value)
                    .map(v -> (T)v)
                    .findFirst();
        }
    }
}
