import logging

logging.basicConfig(level=logging.INFO)

logger = logging.getLogger()

def main() -> None:
    logger.critical("Hello from backend!")


if __name__ == "__main__":
    main()
