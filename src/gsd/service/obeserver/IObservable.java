package gsd.service.obeserver;

/**
 * Implement this interface to make a class observable
 */
public interface IObservable {

	public void addObserver(IObserver observer);

	public void removeObserver(IObserver observer);
}
