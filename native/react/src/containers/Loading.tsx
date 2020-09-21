import React from "react";
import { useDispatch } from "react-redux";

import { Loading } from "~/components/pages";

export default function ConnectedLoading() {
  const dispatch = useDispatch();
  const actions = React.useMemo(() => ({}), [dispatch]);

  return <Loading actions={actions} />
}
