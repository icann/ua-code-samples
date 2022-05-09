FROM python:alpine

WORKDIR /backend

COPY . .

RUN pip3 install .
RUN pip3 install -r src/backend/requirements.txt

ENV FLASK_APP=src/backend/ua_samples

EXPOSE 5000/tcp
CMD [ "flask", "run", "--port", "5000", "--host", "0.0.0.0" ]
