from setuptools import setup, find_packages

setup(
    name='univeral-acceptance-samples',
    version='1.0',
    packages=find_packages(
        where='src',
        include=['ua*'],
        exclude=['bin', 'backend'],
    ),
    package_dir={"": "src"},
    url='https://cofomo.github.io/universal-acceptance/',
    license='MIT',
    author='Julien Bernard',
    author_email='int-eng@cofomo.com',
    description='Python UA samples for IDNA and EAI',
    install_requires=[
        'email-validator==1.1.3',
        'idna==3.3',
    ]
)
