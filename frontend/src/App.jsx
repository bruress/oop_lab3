import { useEffect, useMemo, useState } from 'react';

function App() {
  const [vk, setVk] = useState([]);
  const [tg, setTg] = useState([]);
  const [vkCtor, setVkCtor] = useState('full');
  const [tgCtor, setTgCtor] = useState('token');
  const [editType, setEditType] = useState('vk');
  const [invokeType, setInvokeType] = useState('vk');
  const [parentType, setParentType] = useState('vk');

  const [vkVersion, setVkVersion] = useState('5.199');
  const [vkToken, setVkToken] = useState('vk_new_token');
  const [tgToken, setTgToken] = useState('tg_new');
  const [tgChatId, setTgChatId] = useState('100500');

  const [invokeId, setInvokeId] = useState('');
  const [invokeCommand, setInvokeCommand] = useState('getText');
  const [result, setResult] = useState('{}');
  const [error, setError] = useState('');

  const API = 'http://127.0.0.1:8080/api';

  const loadLists = async () => {
    try {
      setError('');
      const [vkResp, tgResp] = await Promise.all([
        fetch(`${API}/vkapis`),
        fetch(`${API}/tgapis`)
      ]);
      if (!vkResp.ok || !tgResp.ok) throw new Error('Не удалось загрузить списки API');

      const [vkData, tgData] = await Promise.all([vkResp.json(), tgResp.json()]);
      setVk(Array.isArray(vkData) ? vkData : []);
      setTg(Array.isArray(tgData) ? tgData : []);
    } catch (e) {
      setError(e.message || 'Ошибка загрузки');
    }
  };

  useEffect(() => {
    loadLists();
  }, []);

  const ids = useMemo(() => {
    const source = invokeType === 'vk' ? vk : tg;
    return source.map((x) => String(x.id));
  }, [invokeType, vk, tg]);

  useEffect(() => {
    if (invokeType === 'vk') {
      setInvokeCommand('getText');
      setInvokeId(vk.length > 0 ? String(vk[0].id) : '');
    } else {
      setInvokeCommand('sendMessage');
      setInvokeId(tg.length > 0 ? String(tg[0].id) : '');
    }
  }, [invokeType, vk, tg]);

  const addVk = async () => {
    try {
      setError('');
      if (vkCtor === 'full') {
        await fetch(`${API}/vkapis/full`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ version: vkVersion, token: vkToken })
        });
      } else if (vkCtor === 'token') {
        await fetch(`${API}/vkapis/token`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token: vkToken })
        });
      } else {
        await fetch(`${API}/vkapis/default`, { method: 'POST' });
      }
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка добавления VK');
    }
  };

  const addTg = async () => {
    try {
      setError('');
      if (tgCtor === 'full') {
        await fetch(`${API}/tgapis/full`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ botToken: tgToken, chatId: Number(tgChatId || 0) })
        });
      } else if (tgCtor === 'token') {
        await fetch(`${API}/tgapis/token`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ botToken: tgToken })
        });
      } else {
        await fetch(`${API}/tgapis/chat`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ chatId: Number(tgChatId || 0) })
        });
      }
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка добавления TG');
    }
  };

  const removeVk = async (id) => {
    try {
      await fetch(`${API}/vkapis/${id}`, { method: 'DELETE' });
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка удаления VK');
    }
  };

  const removeTg = async (id) => {
    try {
      await fetch(`${API}/tgapis/${id}`, { method: 'DELETE' });
      await loadLists();
    } catch (e) {
      setError(e.message || 'Ошибка удаления TG');
    }
  };

  const invokeInitialize = async () => {
    if (!invokeId) return;
    const base = invokeType === 'vk' ? 'vkapis' : 'tgapis';
    const resp = await fetch(`${API}/${base}/${invokeId}/initialize`);
    setResult(await resp.text());
  };

  const invokeFetchData = async () => {
    if (!invokeId) return;
    const base = invokeType === 'vk' ? 'vkapis' : 'tgapis';
    const resp = await fetch(`${API}/${base}/${invokeId}/fetchdata/${invokeCommand}`);
    setResult(await resp.text());
  };

  const invokeRun = async () => {
    if (!invokeId) return;
    const base = invokeType === 'vk' ? 'vkapis' : 'tgapis';
    const resp = await fetch(`${API}/${base}/${invokeId}/run/${invokeCommand}`);
    const data = await resp.json();
    if (typeof data?.result === 'string') setResult(data.result);
    else setResult(JSON.stringify(data?.result ?? data, null, 2));
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 md:py-10">
      <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight mb-3">Апишечки :3</h1>
      {error && <p className="mb-3 font-semibold text-red-600">{error}</p>}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 items-stretch">
        <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 h-full">
          <h2 className="font-bold mb-4">VK APIs</h2>
          <div className="grid grid-cols-1 md:grid-cols-12 gap-2 mb-2">
            <select className="rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={vkCtor} onChange={e => setVkCtor(e.target.value)}>
              <option value="full">ctor(version,token)</option>
              <option value="token">ctor(token)</option>
              <option value="default">ctor()</option>
            </select>
            {(vkCtor === 'full' || vkCtor === 'default') && (
              <>
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-2" value={vkVersion} onChange={e => setVkVersion(e.target.value)} placeholder="Version" />
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={vkToken} onChange={e => setVkToken(e.target.value)} placeholder="Access token" />
              </>
            )}
            {vkCtor === 'token' && (
              <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-5" value={vkToken} onChange={e => setVkToken(e.target.value)} placeholder="Access token" />
            )}
            <button onClick={addVk} className="w-full min-w-[132px] rounded-xl px-4 py-2 font-bold text-white flex items-center justify-center whitespace-nowrap bg-gradient-to-br from-brandViolet to-brandBlue md:col-span-4">Добавить</button>
          </div>
          <ul className="list-disc pl-5 space-y-3">
            {vk.map(item => (
              <li key={item.id}>
                <b>#{item.id}</b> v{item.version}
                <div className="flex flex-wrap gap-2 mt-2">
                  <button onClick={() => { setInvokeType('vk'); setInvokeId(String(item.id)); setInvokeCommand('getText'); invokeRun(); }} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">getText</button>
                  <button onClick={() => { setInvokeType('vk'); setInvokeId(String(item.id)); setInvokeCommand('getLike'); invokeRun(); }} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">getLike</button>
                  <button onClick={() => removeVk(item.id)} className="rounded-xl px-3 py-2 font-bold text-slate-900 bg-gradient-to-br from-amber-400 to-brandYellow">Delete</button>
                </div>
              </li>
            ))}
          </ul>
        </section>

        <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 h-full">
          <h2 className="font-bold mb-4">TG APIs</h2>
          <div className="grid grid-cols-1 md:grid-cols-12 gap-2 mb-2">
            <select className="rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={tgCtor} onChange={e => setTgCtor(e.target.value)}>
              <option value="full">ctor(token,chatId)</option>
              <option value="token">ctor(token)</option>
              <option value="chat">ctor(chatId)</option>
            </select>
            {tgCtor === 'full' && (
              <>
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-2" value={tgToken} onChange={e => setTgToken(e.target.value)} placeholder="Bot token" />
                <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-3" value={tgChatId} onChange={e => setTgChatId(e.target.value)} placeholder="Chat id" />
              </>
            )}
            {tgCtor === 'token' && (
              <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-5" value={tgToken} onChange={e => setTgToken(e.target.value)} placeholder="Bot token" />
            )}
            {tgCtor === 'chat' && (
              <input className="w-full rounded-xl border border-slate-300 px-3 py-2 md:col-span-5" value={tgChatId} onChange={e => setTgChatId(e.target.value)} placeholder="Chat id" />
            )}
            <button onClick={addTg} className="w-full min-w-[132px] rounded-xl px-4 py-2 font-bold text-white flex items-center justify-center whitespace-nowrap bg-gradient-to-br from-brandViolet to-brandBlue md:col-span-4">Добавить</button>
          </div>
          <ul className="list-disc pl-5 space-y-3">
            {tg.map(item => (
              <li key={item.id}>
                <b>#{item.id}</b> chat {item.chatId}
                <div className="flex flex-wrap gap-2 mt-2">
                  <button onClick={() => { setInvokeType('tg'); setInvokeId(String(item.id)); setInvokeCommand('sendMessage'); invokeRun(); }} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">sendMessage</button>
                  <button onClick={() => { setInvokeType('tg'); setInvokeId(String(item.id)); setInvokeCommand('sendPhoto'); invokeRun(); }} className="rounded-xl px-3 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">sendPhoto</button>
                  <button onClick={() => removeTg(item.id)} className="rounded-xl px-3 py-2 font-bold text-slate-900 bg-gradient-to-br from-amber-400 to-brandYellow">Delete</button>
                </div>
              </li>
            ))}
          </ul>
        </section>
      </div>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Добавление связанных данных</h2>
        <div className="text-slate-700">Пока не подключено: нужны endpoint-ы для posts/payloads</div>
      </section>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Редактирование по ID</h2>
        <div className="text-slate-700">Пока не подключено: нужны endpoint-ы update</div>
      </section>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Вызов метода с фронта</h2>
        <div className="flex flex-col md:flex-row gap-2 mb-2">
          <select className="rounded-xl border border-slate-300 px-3 py-2" value={invokeType} onChange={e => setInvokeType(e.target.value)}>
            <option value="vk">VK</option>
            <option value="tg">TG</option>
          </select>
          <select className="rounded-xl border border-slate-300 px-3 py-2" value={invokeId} onChange={e => setInvokeId(e.target.value)}>
            {ids.length === 0 && <option value="">Нет записей</option>}
            {ids.map((id) => <option key={id} value={id}>ID: {id}</option>)}
          </select>
          <select className="rounded-xl border border-slate-300 px-3 py-2" value={invokeCommand} onChange={e => setInvokeCommand(e.target.value)}>
            {invokeType === 'vk' ? (
              <>
                <option value="getText">getText</option>
                <option value="getLike">getLike</option>
              </>
            ) : (
              <>
                <option value="sendMessage">sendMessage</option>
                <option value="sendPhoto">sendPhoto</option>
              </>
            )}
          </select>
          <button onClick={invokeInitialize} className="rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandBlue to-brandViolet">Initialize</button>
          <button onClick={invokeFetchData} className="rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">FetchData</button>
          <button onClick={invokeRun} className="rounded-xl px-4 py-2 font-bold text-white bg-gradient-to-br from-brandViolet to-brandBlue">Выполнить</button>
          <button onClick={loadLists} className="rounded-xl px-4 py-2 font-bold text-slate-900 bg-gradient-to-br from-amber-400 to-brandYellow">Обновить списки</button>
        </div>
      </section>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Родительские методы (SocialApi)</h2>
        <div className="text-slate-700">Используй блок "Вызов метода с фронта" кнопками Initialize/FetchData</div>
      </section>

      <section className="rounded-3xl bg-white/85 backdrop-blur border-2 border-brandViolet/20 shadow-[0_12px_30px_rgba(29,78,216,0.15)] p-5 mt-5">
        <h2 className="text-2xl font-bold mb-3">Результат вызова методов</h2>
        <div className="whitespace-pre-wrap bg-slate-100 rounded-xl p-3">{result}</div>
      </section>
    </div>
  );
}

export default App;
