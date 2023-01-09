public class myExceptions {

    public static class IncorrectReservationCode extends Exception {
        public IncorrectReservationCode(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class IncorrectDestinationName extends Exception {
        public IncorrectDestinationName(String errorMessage) {
            super(errorMessage);
        }
    }
}
