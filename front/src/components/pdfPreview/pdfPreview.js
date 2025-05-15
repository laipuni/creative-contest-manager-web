import {useEffect, useMemo} from "react";

const PDFPreview = ({ blob }) => {
    const blobUrl = useMemo(() => URL.createObjectURL(blob), [blob]);

    useEffect(() => {
        return () => URL.revokeObjectURL(blobUrl); // 메모리 해제
    }, [blobUrl]);

    return (
        <embed src={blobUrl} type="application/pdf" width="500px" height="500px"/>
    );
};

export default PDFPreview;
