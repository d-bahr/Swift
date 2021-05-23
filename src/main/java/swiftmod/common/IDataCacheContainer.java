package swiftmod.common;

public interface IDataCacheContainer<T extends DataCache>
{
    T getCache();
}
