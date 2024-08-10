from sqlalchemy import create_engine, Column, String, Integer, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

Base = declarative_base()

class User(Base):
    __tablename__ = 'utilizatori'

    uid = Column(Integer, primary_key=True, nullable=False)
    username = Column(String(128), unique=True, nullable=False)
    password = Column(String(128), nullable=False)
    role = Column(String(128), nullable=False)

class RevokedToken(Base):
    __tablename__ = 'revokedTokens'
    jti = Column(String,primary_key=True)
    expirationDate = Column(DateTime)

'''
db_url = "mysql://root:password@localhost:3307/pos"


engine = create_engine(db_url)

Base.metadata.create_all(engine)

Session = sessionmaker(bind=engine)
session = Session()

session.commit()
'''
