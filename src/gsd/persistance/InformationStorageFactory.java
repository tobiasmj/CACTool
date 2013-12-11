package gsd.persistance;

/**
 * Factory for getting storage objects
 */
public class InformationStorageFactory {

	public static InformationStorage getStorageAdapter() {
		InformationStorage storage = (InformationStorage) FakeDB.getInstance();
		return storage;
	}
}
