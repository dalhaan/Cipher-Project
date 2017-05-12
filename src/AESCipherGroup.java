import java.util.List;

public class AESCipherGroup extends Node {
	private static final long serialVersionUID = -6844481207537277284L;
	List<Node> children;
	
	public void addChild(Node child) {
		children.add(child);
	}

	@Override
	public void decrypt() {
		for (Node child : children) {
			child.decrypt();
		}
	}
}
