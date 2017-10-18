const getApiHost = () => {
    return process.env.NODE_ENV === 'development' ? `http://${window.location.hostname}:5151` : '';
};

export {getApiHost};
