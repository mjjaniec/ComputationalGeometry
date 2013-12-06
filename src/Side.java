
public enum Side {
    NONE {
        @Override
        public Side invert() {
            return NONE;
        }
    }, TOP {
        @Override
        public Side invert() {
            return BOTTOM;
        }
    }, BOTTOM {
        @Override
        public Side invert() {
            return TOP;
        }
    }, LEFT {
        @Override
        public Side invert() {
            return RIGHT;
        }
    }, RIGHT {
        @Override
        public Side invert() {
            return LEFT;
        }
    };

    public abstract Side invert();
}